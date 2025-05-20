package priceComparator.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import priceComparator.dtos.DiscountedProductDTO;
import priceComparator.models.Discount;
import priceComparator.models.Product;
import priceComparator.services.DiscountQueryService;

import java.util.List;

/**
 * REST controller that handles HTTP requests related to analytics of {@link Product} and {@link Discount} entities.
 * Provides endpoints to see top/ current/ new discounts .
 */
@RestController
@RequestMapping("/analytics")
public class AnalyticsController {
    @Autowired
    private DiscountQueryService discountQueryService;
    /**
     * Retrieves all currently active discounts.
     *
     * @return a list of {@link DiscountedProductDTO} representing active discounts.
     */
    @GetMapping("/currentDiscounts")
    public List<DiscountedProductDTO> getAllCurrentDiscounts() {
        return discountQueryService.getAllCurrentDiscounts();
    }

    /**
     * Retrieves top discounted products sorted by discount percentage.
     *
     * @param limit the maximum number of results to return (default is 5).
     * @return a list of {@link DiscountedProductDTO} with highest discounts.
     */
    @GetMapping("/topDiscounts")
    public List<DiscountedProductDTO> getTopDiscountsByDiscount(@RequestParam(defaultValue = "5") int limit) {
        return discountQueryService.getTopDiscountsByDiscount(limit);
    }

    /**
     * Retrieves newly added discounts.
     *
     * @return a list of {@link DiscountedProductDTO} with info of newly added discounts.
     */
    @GetMapping("/newDiscounts")
    public List<DiscountedProductDTO> getNewDiscounts(){
        return discountQueryService.getNewDiscounts();
    }
}
