package priceComparator.controllers;

import priceComparator.models.Discount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import priceComparator.services.DiscountService;

import java.util.List;

@RestController
@RequestMapping("/discounts")
public class DiscountController {

    @Autowired
    private DiscountService discountService;

    @GetMapping
    public List<Discount> getAllDiscounts(){
        return discountService.getAllDiscounts();
    }

    @PostMapping
    public Discount createDiscount(@RequestBody Discount discount){
        return discountService.saveDiscount(discount);
    }

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
}
