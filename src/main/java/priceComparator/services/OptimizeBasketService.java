package priceComparator.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import priceComparator.dtos.ProductDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OptimizeBasketService {

    @Autowired
    PriceEvaluatorService priceEvaluatorService;

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

        List<ProductDTO> optimizedList = new ArrayList<>();

        for (String productName : productNames) {
            Optional<ProductDTO> best = priceEvaluatorService.getProductWithLowestPrice(productName);
            best.ifPresent(optimizedList::add);
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
