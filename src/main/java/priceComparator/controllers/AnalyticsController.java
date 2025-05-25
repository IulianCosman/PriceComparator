package priceComparator.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import priceComparator.dtos.ProductDTO;
import priceComparator.models.Discount;
import priceComparator.models.Product;
import priceComparator.services.AnalyticsService;

import java.util.List;

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


}
