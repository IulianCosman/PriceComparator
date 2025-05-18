package priceComparator.controllers;

import priceComparator.models.Discount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import priceComparator.dtos.DiscountedProductDTO;
import priceComparator.services.DiscountQueryService;
import priceComparator.services.DiscountService;

import java.util.List;

/**
 * REST controller that handles HTTP requests related to {@link Discount} entities.
 * Provides endpoints to manage discounts, view current and top deals.
 */
@RestController
@RequestMapping("/discounts")
public class DiscountController {

    @Autowired
    private DiscountService discountService;

    @Autowired
    private DiscountQueryService discountQueryService;
    /**
     * Retrieves all discounts from the database (including expired).
     *
     * @return a list of all {@link Discount} entities.
     */
    @GetMapping
    public List<Discount> getAllDiscounts(){
        return discountService.getAllDiscounts();
    }

    /**
     * Creates a new discount in the database.
     *
     * @param discount the {@link Discount} entity to save.
     * @return the saved {@link Discount}.
     */
    @PostMapping
    public Discount createDiscount(@RequestBody Discount discount){
        return discountService.saveDiscount(discount);
    }

    /**
     * Retrieves a discount by its unique ID.
     *
     * @param id the discount ID (from the path).
     * @return the {@link Discount} if found, otherwise 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Discount> getDiscountById(@PathVariable Long id){
        Discount discount = discountService.getDiscountById(id);
        if(discount != null){
            return ResponseEntity.ok(discount);
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Retrieves all currently active discounts.
     *
     * @return a list of {@link DiscountedProductDTO} representing active discounts.
     */
    @GetMapping("/current")
    public List<DiscountedProductDTO> getAllCurrentDiscounts() {
        return discountQueryService.getAllCurrentDiscounts();
    }

    /**
     * Retrieves top discounted products sorted by discount percentage.
     *
     * @param limit the maximum number of results to return (default is 5).
     * @return a list of {@link DiscountedProductDTO} with highest discounts.
     */
    @GetMapping("/topDiscount")
    public List<DiscountedProductDTO> getTopDiscountsByDiscount(@RequestParam(defaultValue = "5") int limit) {
        return discountQueryService.getTopDiscountsByDiscount(limit);
    }

}
