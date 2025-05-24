package priceComparator.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import priceComparator.dtos.PriceHistoryPointDTO;
import priceComparator.dtos.ProductDTO;
import priceComparator.models.Discount;
import java.util.Map;
import priceComparator.models.Product;
import priceComparator.services.AnalyticsService;

import java.util.List;
import java.util.Optional;

/**
 * REST controller that handles HTTP requests related to analytics of {@link Product} and {@link Discount} entities.
 * Provides endpoints to see top/ current/ new discounts, optimize basket, see price history .
 */
@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;


    /**
     * Retrieves all currently active discounts.
     *
     * @return a list of {@link ProductDTO} representing active discounts.
     */
    @GetMapping("/currentDiscounts")
    public ResponseEntity<List<ProductDTO>> getAllCurrentDiscounts() {
        return ResponseEntity.ok(analyticsService.getAllCurrentDiscounts());
    }

    /**
     * Retrieves top discounted products sorted by discount percentage.
     *
     * @param limit the maximum number of results to return (default is 5).
     * @return a list of {@link ProductDTO} with highest discounts.
     */
    @GetMapping("/topDiscounts")
    public ResponseEntity<List<ProductDTO>> getTopDiscountsByDiscount(@RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(analyticsService.getTopDiscountsByDiscount(limit));
    }

    /**
     * Retrieves newly added discounts.
     *
     * @return a list of {@link ProductDTO} with info of newly added discounts.
     */
    @GetMapping("/newDiscounts")
    public ResponseEntity<List<ProductDTO>> getNewDiscounts(){
        return ResponseEntity.ok(analyticsService.getNewDiscounts());
    }

    /**
     * Returns the best-priced version of each product in the user's basket,
     * after evaluating prices and discounts across all stores.
     *
     * @param productNames the list of product names to evaluate
     * @return a list of optimized {@link ProductDTO} objects, one per product
     */
    @PostMapping("/optimizeBasket")
    public ResponseEntity<List<ProductDTO>> optimizeBasket(@RequestBody List<String> productNames) {
        List<ProductDTO> optimizedList = analyticsService.getOptimizedProductList(productNames);
        return ResponseEntity.ok(optimizedList);
    }

    /**
     * Returns an optimized shopping list grouped by store name.
     * For each product in the basket, this finds the cheapest available offer (with discount if applicable),
     * and organizes them into separate lists per store.
     *
     * @param productNames the list of product names the user wants to buy
     * @return a map: storeName → list of optimized {@link ProductDTO} entries for that store
     */
    @PostMapping("/optimizeBasketByStore")
    public ResponseEntity<Map<String, List<ProductDTO>>> optimizeBasketByStore(
            @RequestBody List<String> productNames) {

        Map<String, List<ProductDTO>> grouped = analyticsService.getOptimizedBasketGroupedByStore(productNames);
        return ResponseEntity.ok(grouped);
    }

    /**
     * Returns the full price history timeline of a given product name,
     * including price changes and discount intervals. Optionally filters by store, brand, or category.
     *
     * @param productName the product name to fetch history for
     * @param storeName optional store name to filter by
     * @param category optional product category
     * @param brand optional brand name
     * @return list of time-segmented price points
     */
    @GetMapping("/priceHistory")
    public ResponseEntity<List<PriceHistoryPointDTO>> getPriceHistory(
            @RequestParam String productName,
            @RequestParam(required = false) String storeName,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand
    ) {
        List<PriceHistoryPointDTO> history = analyticsService.getPriceHistory(
                productName,
                Optional.ofNullable(storeName),
                Optional.ofNullable(category),
                Optional.ofNullable(brand)
        );
        return ResponseEntity.ok(history);
    }

}
