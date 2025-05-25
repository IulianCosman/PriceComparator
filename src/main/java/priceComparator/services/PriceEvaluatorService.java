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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service used for basket optimizing and alerts.
 */
@Service
public class PriceEvaluatorService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private ProductMapper productMapper;

    /**
     * Searches for the {@link Product} with the best (lowest) price for a given name across all stores.
     * Applies current discounts if available.
     *
     * @param productName the name of the {@link Product} for which we are searching the lowest price.
     * @return a {@link ProductDTO} containing the info of product.
     */
    public Optional<ProductDTO> getProductWithLowestPrice(String productName) {
        Optional<ProductDTO> best = Optional.empty();
        Map<String, Discount> discountMap = getActiveDiscountMap();
        List<String> stores = productRepository.findAllDistinctStoreNames();

        // Iterate through the stores
        for (String store : stores) {
            // Find the most recent product by name + store
            Optional<Product> optionalProduct = productRepository
                    .findTopByNameIgnoreCaseAndStoreNameIgnoreCaseOrderByDateAddedDesc(productName, store);

            // Skip if the product does not exist in the store
            if (optionalProduct.isEmpty()) continue;

            Product product = optionalProduct.get();
            // We assume one discount can be available at a time
            Discount discount = discountMap.get(product.getProductId() + "|" + store.toLowerCase());

            ProductDTO dto = (discount != null)
                    ? productMapper.mapToDTOWithDiscount(discount,
                    productRepository.findTopByProductIdAndStoreNameIgnoreCaseOrderByDateAddedDesc(
                            discount.getProductId(), discount.getStoreName())) // discount exists, so apply
                    : productMapper.mapToDTOWithoutDiscount(product); // no discount, so raw price

            // Keep the one with the lowest price
            if (best.isEmpty() || dto.getDiscountedPrice() < best.get().getDiscountedPrice()) {
                best = Optional.of(dto);
            }
        }
        return best;
    }

    /**
     * Builds a map for quick discount lookup using productId + storeName as key.
     * @return map of  <(productId|store)| Discount>
     */
    private Map<String, Discount> getActiveDiscountMap() {
        LocalDate today = LocalDate.now();
        // We assume one discount active at a time
        List<Discount> activeDiscounts = discountRepository.findActiveDiscounts(today);

        // Group discounts for quick lookup by productId+store
        return activeDiscounts.stream()
                .collect(Collectors.toMap(
                        d -> d.getProductId() + "|" + d.getStoreName().toLowerCase(),
                        d -> d,
                        (d1, d2) -> d1 // if duplicate, keep first
                ));
    }
}

