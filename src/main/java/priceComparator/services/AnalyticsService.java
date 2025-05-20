package priceComparator.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import priceComparator.dtos.ProductDTO;
import priceComparator.mappers.ProductMapper;
import priceComparator.models.Discount;
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

}
