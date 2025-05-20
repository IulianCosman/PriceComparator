package priceComparator.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import priceComparator.dtos.ProductDTO;
import priceComparator.models.Discount;
import java.util.Map;
import priceComparator.models.Product;
import priceComparator.services.AnalyticsService;

import java.util.List;

/**
 * REST controller that handles HTTP requests related to analytics of {@link Product} and {@link Discount} entities.
 * Provides endpoints to see top/ current/ new discounts .
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
    public List<ProductDTO> getAllCurrentDiscounts() {
        return analyticsService.getAllCurrentDiscounts();
    }

    /**
     * Retrieves top discounted products sorted by discount percentage.
     *
     * @param limit the maximum number of results to return (default is 5).
     * @return a list of {@link ProductDTO} with highest discounts.
     */
    @GetMapping("/topDiscounts")
    public List<ProductDTO> getTopDiscountsByDiscount(@RequestParam(defaultValue = "5") int limit) {
        return analyticsService.getTopDiscountsByDiscount(limit);
    }

    /**
     * Retrieves newly added discounts.
     *
     * @return a list of {@link ProductDTO} with info of newly added discounts.
     */
    @GetMapping("/newDiscounts")
    public List<ProductDTO> getNewDiscounts(){
        return analyticsService.getNewDiscounts();
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
}
