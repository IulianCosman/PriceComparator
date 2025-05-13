package priceComparator.controllers;

import priceComparator.models.Discount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import priceComparator.models.DiscountedProductDTO;
import priceComparator.services.DiscountService;

import java.util.List;

@RestController
@RequestMapping("/discounts")
public class DiscountController {

    @Autowired
    private DiscountService discountService;

    // Get all discounts ever
    @GetMapping
    public List<Discount> getAllDiscounts(){
        return discountService.getAllDiscounts();
    }

    // Save/create a discount
    @PostMapping
    public Discount createDiscount(@RequestBody Discount discount){
        return discountService.saveDiscount(discount);
    }

    // Get a discount by its id
    @GetMapping("/{id}")
    public ResponseEntity<Discount> getDiscountById(@RequestBody Long id){
        Discount discount = discountService.getDiscountById(id);
        if(discount != null){
            return ResponseEntity.ok(discount);
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }

    // Get all current discounts
    @GetMapping("/current")
    public List<DiscountedProductDTO> getAllCurrentDiscounts() {
        return discountService.getAllCurrentDiscounts();
    }

    // Get top discounts by price per unit
    @GetMapping("/topPricePerUnit")
    public List<DiscountedProductDTO> getTopDiscountsByPricePerUnit(@RequestParam(defaultValue = "5") int limit) {
        return discountService.getTopDiscountsByPricePerUnit(limit);
    }

    // Get top discounts by discount percentage
    @GetMapping("/topDiscount")
    public List<DiscountedProductDTO> getTopDiscountsByDiscount(@RequestParam(defaultValue = "5") int limit) {
        return discountService.getTopDiscountsByDiscount(limit);
    }

}
