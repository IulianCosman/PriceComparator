package priceComparator.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import priceComparator.dtos.ProductDTO;
import priceComparator.models.Currency;
import priceComparator.models.Discount;
import priceComparator.models.PackageUnit;
import priceComparator.models.Product;
import priceComparator.repositories.DiscountRepository;
import priceComparator.repositories.ProductRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
                .map(this::mapToDTOWithDiscount)
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
     * Maps a {@link Discount} entity to a {@link ProductDTO}, computing
     * the discounted price, converting to RON if needed, and calculating price per unit.
     *
     * @param discount the discount entity to convert.
     * @return a fully populated DTO with calculated fields.
     */
    private ProductDTO mapToDTOWithDiscount(Discount discount) {
        Optional<Product> optionalProduct = productRepository
                .findTopByProductIdAndStoreNameIgnoreCaseOrderByDateAddedDesc(
                        discount.getProductId(), discount.getStoreName());

        if (optionalProduct.isEmpty()) {
            throw new IllegalArgumentException("No matching product found for discount for "
                    + discount.getName() + " from " + discount.getStoreName());
        }

        Product product = optionalProduct.get();

        double originalPrice = product.getPrice();
        double discountedPrice = originalPrice * (1 - discount.getPercentage() / 100.0);
        Currency currency = product.getCurrency();

        originalPrice = convertToRon(originalPrice, currency);
        discountedPrice = convertToRon(discountedPrice, currency);

        PackageUnit unit = normalizeUnit(product.getPackageUnit());
        double quantity = normalizeQuantity(product.getPackageUnit(), product.getPackageQuantity());

        double pricePerUnit = discountedPrice / quantity;

        return new ProductDTO(
                discount.getName(),
                discount.getBrand(),
                discount.getProductId(),
                discount.getCategory(),
                round(originalPrice),
                discount.getPercentage(),
                round(discountedPrice),
                round(pricePerUnit) + " RON per " + unit,
                discount.getStoreName()
        );
    }

    /**
     * Maps a {@link Product} entity to a {@link ProductDTO}
     * converting to RON if needed, and calculating price per unit.
     *
     * @param product the product entity to convert.
     * @return a fully populated DTO with calculated fields.
     */
    private ProductDTO mapToDTOWithoutDiscount(Product product) {
        double originalPrice = convertToRon(product.getPrice(), product.getCurrency());
        PackageUnit unit = normalizeUnit(product.getPackageUnit());
        double quantity = normalizeQuantity(product.getPackageUnit(), product.getPackageQuantity());
        double pricePerUnit = originalPrice / quantity;

        return new ProductDTO(
                product.getName(),
                product.getBrand(),
                product.getProductId(),
                product.getCategory(),
                round(originalPrice),
                0,
                round(originalPrice),
                round(pricePerUnit) + " RON per " + unit,
                product.getStoreName()
        );
    }

    /**
     * Utility method to round a double value to 2 decimal places.
     *
     * @param value the value to round.
     * @return the rounded value.
     */
    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
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
                .map(this::mapToDTOWithDiscount)
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
                        ? mapToDTOWithDiscount(discount) // discount exists, so apply
                        : mapToDTOWithoutDiscount(product); // no discount, so raw price

                // Keep the one with the lowest price
                if (best == null || extractFinalPrice(dto) < extractFinalPrice(best)) {
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
     * Extracts the final discounted price from a {@link ProductDTO}.
     * This is used for price comparisons between different store offers.
     *
     * @param dto the product DTO containing price information.
     * @return the final discounted price of the product.
     */
    private double extractFinalPrice(ProductDTO dto) {
        return dto.getDiscountedPrice();
    }

    /**
     * Converts the given price to RON using fixed exchange rates for USD and EUR.
     * Assumes that RON values do not require conversion.
     * Used for fair price per unit comparison.
     *
     * @param price    the original price.
     * @param currency the currency in which the price is expressed.
     * @return the equivalent price in RON.
     */
    private double convertToRon(double price, Currency currency) {
        switch (currency) {
            case USD: return price * 4.6;
            case EUR: return price * 5.0;
            default: return price;
        }
    }

    /**
     * Normalizes the quantity to base units (kg or l) for proper price-per-unit calculation.
     * Converts grams (g) and milliliters (ml) to kilograms and liters respectively.
     *
     * @param unit     the current packaging unit.
     * @param quantity the quantity in the current unit.
     * @return the quantity converted to base unit if applicable.
     */
    private double normalizeQuantity(PackageUnit unit, double quantity) {
        if (unit == PackageUnit.g || unit == PackageUnit.ml) {
            return quantity / 1000;
        }
        return quantity;
    }

    /**
     * Converts units like grams (g) and milliliters (ml) to their normalized
     * counterparts: kilograms (kg) and liters (l). Other units remain unchanged.
     *
     * @param unit the original packaging unit.
     * @return the normalized packaging unit.
     */
    private PackageUnit normalizeUnit(PackageUnit unit) {
        if (unit == PackageUnit.g) return PackageUnit.kg;
        if (unit == PackageUnit.ml) return PackageUnit.l;
        return unit;
    }
}
