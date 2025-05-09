package services;

import models.Discount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repositories.DiscountRepository;

import java.util.List;

@Service
public class DiscountService {

    @Autowired
    private DiscountRepository discountRepository;

    public List<Discount> getAllDiscounts(){
        return discountRepository.findAll();
    }

    public Discount saveDiscount(Discount discount){
        return discountRepository.save(discount);
    }

    public Discount getDiscountById(Long id){
        return discountRepository.findById(id).orElse(null);
    }
}
