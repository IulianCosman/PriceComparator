package priceComparator.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import priceComparator.dtos.ProductDTO;
import priceComparator.models.Discount;
import priceComparator.models.Product;
import priceComparator.services.OptimizeBasketService;

import java.util.List;
import java.util.Map;

/**
 * REST controller that handles HTTP requests related to optimizing a basket of {@link Product} entities
 * applying a {@link Discount} where it is needed, so that the price is the lowest.
 */
@RestController
@RequestMapping("/optimizeBasket")
public class OptimizeBasketController {

    @Autowired
    OptimizeBasketService optimizeBasketService;

    /**
     * Returns the best-priced version of each product in the user's basket,
     * after evaluating prices and discounts across all stores.
     *
     * @param productNames the list of product names to evaluate
     * @return a list of optimized {@link ProductDTO} objects, one per product
     */
    @PostMapping("/")
    public ResponseEntity<List<ProductDTO>> optimizeBasket(@RequestBody List<String> productNames) {
        List<ProductDTO> optimizedList = optimizeBasketService.getOptimizedProductList(productNames);
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
    @PostMapping("/byStore")
    public ResponseEntity<Map<String, List<ProductDTO>>> optimizeBasketByStore(
            @RequestBody List<String> productNames) {

        Map<String, List<ProductDTO>> grouped = optimizeBasketService.getOptimizedBasketGroupedByStore(productNames);
        return ResponseEntity.ok(grouped);
    }
}
