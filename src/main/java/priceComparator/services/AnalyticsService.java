package priceComparator.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import priceComparator.dtos.PriceHistoryPointDTO;
import priceComparator.dtos.ProductDTO;
import priceComparator.mappers.ProductMapper;
import priceComparator.models.Discount;
import priceComparator.models.Product;
import priceComparator.repositories.DiscountRepository;
import priceComparator.repositories.ProductRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static priceComparator.mappers.ProductMapper.round;

/**
 * Service layer for managing business logic related to analytics of {@link Discount} and {@link Product}.
 * Handles fetching discounts, converting to DTOs, and calculating discount metrics.
 */
@Service
public class AnalyticsService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private ProductMapper productMapper;


    /**
     * Retrieves the discounts added today/ yesterday.
     *
     * @return a list of {@link ProductDTO} with info of the newly added discounts.
     */
    public List<ProductDTO> getNewDiscounts(){
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        List<Discount> newDiscounts = discountRepository.findByDateAddedIn(List.of(today, yesterday));

        return newDiscounts.stream()
                .map(discount -> productMapper.mapToDTOWithDiscount(
                        discount,
                        productRepository.findTopByProductIdAndStoreNameIgnoreCaseOrderByDateAddedDesc(
                                discount.getProductId(),
                                discount.getStoreName()
                        )
                ))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the top N discounts sorted by discount percentage in descending order.
     *
     * @param topN the number of top discounts to return.
     * @return a sorted list of {@link ProductDTO} by discount percentage.
     */
    public List<ProductDTO> getTopDiscountsByDiscount(int topN){
        return getAllCurrentDiscounts().stream()
                .sorted((d1,d2) -> d2.getDiscountPercentage().compareTo(d1.getDiscountPercentage()))
                .limit(topN)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all currently active discounts and maps them to DTOs.
     * Only discounts whose date range includes the current date are included.
     *
     * @return a list of {@link ProductDTO} representing current discounts.
     */
    public List<ProductDTO> getAllCurrentDiscounts(){
        LocalDate now = LocalDate.now();
        List<Discount> activeDiscounts = discountRepository.findActiveDiscounts(now);
        return activeDiscounts.stream()
                .map(discount -> productMapper.mapToDTOWithDiscount(
                        discount,
                        productRepository.findTopByProductIdAndStoreNameIgnoreCaseOrderByDateAddedDesc(
                                discount.getProductId(),
                                discount.getStoreName()
                        )
                ))
                .collect(Collectors.toList());
    }

    /**
     * Given a list of product names, finds the most cost-effective option (per product)
     * by evaluating the latest product price from each store and applying any active discount.
     * For each product name:
     * - Checks all available stores
     * - Applies store-specific discounts if available
     * - Compares final prices (after discount)
     * - Returns only the best-priced version (one per product)
     *
     * @param productNames list of product names to optimize
     * @return list of {@link ProductDTO} objects, each representing the cheapest store-specific offer
     */
    public List<ProductDTO> getOptimizedProductList(List<String> productNames) {
        LocalDate today = LocalDate.now();
        List<String> stores = productRepository.findAllDistinctStoreNames();
        List<Discount> activeDiscounts = discountRepository.findActiveDiscounts(today);

        // Group discounts for quick lookup by productId+store
        Map<String, Discount> discountMap = activeDiscounts.stream()
                .collect(Collectors.toMap(
                        d -> d.getProductId() + "|" + d.getStoreName().toLowerCase(),
                        d -> d,
                        (d1, d2) -> d1 // if duplicate, keep first
                ));

        List<ProductDTO> optimizedList = new ArrayList<>();

        for (String productName : productNames) {
            ProductDTO best = null;

            for (String store : stores) {
                // Find the most recent product by name + store
                Optional<Product> optionalProduct = productRepository
                        .findTopByNameIgnoreCaseAndStoreNameIgnoreCaseOrderByDateAddedDesc(productName, store);

                // Skip if the product does not exist in the store
                if (optionalProduct.isEmpty()) continue;

                Product product = optionalProduct.get();
                Discount discount = discountMap.get(product.getProductId() + "|" + store.toLowerCase());

                ProductDTO dto = (discount != null)
                        ? productMapper.mapToDTOWithDiscount(discount,
                        productRepository.findTopByProductIdAndStoreNameIgnoreCaseOrderByDateAddedDesc(
                                discount.getProductId(), discount.getStoreName())) // discount exists, so apply
                        : productMapper.mapToDTOWithoutDiscount(product); // no discount, so raw price

                // Keep the one with the lowest price
                if (best == null || dto.getDiscountedPrice() < best.getDiscountedPrice()) {
                    best = dto;
                }
            }

            if (best != null) {
                optimizedList.add(best);
            }
        }

        return optimizedList;
    }

    /**
     * Optimizes a user's basket by finding the best price (after discounts) for each product,
     * then groups the best options by store name.
     * This helps split the shopping list into per-store sub-lists.
     *
     * @param productNames list of product names in the user's basket
     * @return a map where each key is a store name, and the value is the list of cheapest products from that store
     */
    public Map<String, List<ProductDTO>> getOptimizedBasketGroupedByStore(List<String> productNames) {
        // Reuse the existing optimizer logic to get best-priced versions of each product
        List<ProductDTO> optimizedList = getOptimizedProductList(productNames);

        // Group the best deals by store name
        return optimizedList.stream()
                .collect(Collectors.groupingBy(ProductDTO::getStoreName));
    }

    /**
     * Retrieves the complete price history of a product, including all historical price entries
     * and any applied discounts, grouped and segmented by store.
     * The result allows a frontend to construct a time-series graph of price changes.
     * Supports optional filtering by store, product category, and brand.
     * Logic:
     * - Gathers all product entries for the given product name, ordered chronologically.
     * - Filters the products based on optional store/category/brand.
     * - Groups products by store and iterates through each store's entries.
     * For each product entry:
     * - Determines the time range it was valid (from its dateAdded until the next entry or today).
     * - Identifies overlapping discounts during that time frame.
     * - Splits the time interval into segments:
     *     - Un-discounted before a discount starts,
     *     - Discounted during discount interval,
     *     - Un-discounted after discount ends (if applicable).
     *
     * @param productName the product to get history for (required)
     * @param storeName optional store to filter by
     * @param category optional category to filter by
     * @param brand optional brand to filter by
     * @return a list of {@link PriceHistoryPointDTO}, each representing a price value over a specific date interval
     */

    public List<PriceHistoryPointDTO> getPriceHistory(
            String productName,
            Optional<String> storeName,
            Optional<String> category,
            Optional<String> brand
    ) {
        LocalDate today = LocalDate.now();

        // Fetch product entries by name, ordered by date
        List<Product> allProducts = productRepository.findByNameIgnoreCaseOrderByDateAddedAsc(productName)
                .stream()
                .filter(p -> storeName.map(s -> s.equalsIgnoreCase(p.getStoreName())).orElse(true))
                .filter(p -> category.map(c -> c.equalsIgnoreCase(p.getCategory())).orElse(true))
                .filter(p -> brand.map(b -> b.equalsIgnoreCase(p.getBrand())).orElse(true))
                .toList();

        if (allProducts.isEmpty()) return List.of();

        // Group product entries by store
        Map<String, List<Product>> groupedByStore = allProducts.stream()
                .collect(Collectors.groupingBy(p -> p.getStoreName().toLowerCase()));

        List<Discount> discounts = discountRepository.findByNameIgnoreCase(productName);
        List<PriceHistoryPointDTO> history = new ArrayList<>();

        // Iterate through the stores
        for (var entry : groupedByStore.entrySet()) {
            String store = entry.getKey();
            List<Product> products = entry.getValue();

            // Iterate through the product entries of this store
            for (int i = 0; i < products.size(); i++) {
                Product current = products.get(i);

                // DateFrom for this entry is set as the date it was added
                LocalDate dateFrom = current.getDateAdded();

                // DateTo for this entry is set as the date when the next entry was added
                // If this is the latest entry the dateTo is set to today
                LocalDate dateTo = (i < products.size() - 1) ? products.get(i + 1).getDateAdded() : today;

                double originalPrice = ProductMapper.convertToRon(current.getPrice(), current.getCurrency());

                // See if any discounts were available at the time when the entry with this price was available
                List<Discount> overlapping = discounts.stream()
                        .filter(d -> d.getStoreName().equalsIgnoreCase(store))
                        .filter(d -> !d.getDateTo().isBefore(dateFrom) && !d.getDateFrom().isAfter(dateTo))
                        .toList();

                if (overlapping.isEmpty()) {
                    // There are no discounts overlapping, so we just add the price history point
                    history.add(new PriceHistoryPointDTO(dateFrom, dateTo, round(originalPrice), false, current.getStoreName(), current.getBrand()));
                } else {
                    LocalDate segmentStart = dateFrom;
                    for (Discount d : overlapping) {

                        // First iteration:
                        // Make sure the discountStart is after the dateFrom for the product
                        // Ensures we don't apply a discount to an entry before it existed

                        // Other iterations:
                        // We use the correct discountStart
                        LocalDate discountStart = Collections.max(List.of(dateFrom, d.getDateFrom()));

                        // Same logic as above
                        LocalDate discountEnd = Collections.min(List.of(dateTo, d.getDateTo()));

                        // If the discount is applied after some time we add a price history point
                        // For the un-discounted price in that time frame before the discount
                        if (segmentStart.isBefore(discountStart)) {
                            history.add(new PriceHistoryPointDTO(segmentStart, discountStart, round(originalPrice), false, current.getStoreName(), current.getBrand()));
                        }

                        // We add a price history point for the discounted Price
                        double discountedPrice = round(originalPrice * (1 - d.getPercentage() / 100.0));
                        history.add(new PriceHistoryPointDTO(discountStart, discountEnd, discountedPrice, true, current.getStoreName(), current.getBrand()));

                        // Move segmentStart forward
                        segmentStart = discountEnd;
                    }

                    // Add a price history point (if it is needed) for the un-discounted price after all discounts ended
                    if (segmentStart.isBefore(dateTo)) {
                        history.add(new PriceHistoryPointDTO(segmentStart, dateTo, round(originalPrice), false, current.getStoreName(), current.getBrand()));
                    }
                }
            }
        }

        return history;
    }

}
