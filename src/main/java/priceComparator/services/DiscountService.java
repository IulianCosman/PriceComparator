package priceComparator.services;

import priceComparator.models.Discount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import priceComparator.repositories.DiscountRepository;
import java.util.List;

/**
 * Service layer for managing business logic related to {@link Discount}.
 * Handles fetching discounts, etc.
 */
@Service
public class DiscountService {

    @Autowired
    private DiscountRepository discountRepository;


    /**
     * Retrieves all discounts from the database (active or not).
     *
     * @return a list of all {@link Discount} entities.
     */
    public List<Discount> getAllDiscounts(){
        return discountRepository.findAll();
    }

    /**
     * Saves a new discount.
     *
     * @param discount the {@link Discount} to save.
     * @return the persisted {@link Discount} entity.
     */
    public Discount saveDiscount(Discount discount){
        return discountRepository.save(discount);
    }

    /**
     * Retrieves a discount by its unique identifier.
     *
     * @param id the ID of the discount.
     * @return the found {@link Discount} or null if not found.
     */
    public Discount getDiscountById(Long id){
        return discountRepository.findById(id).orElse(null);
    }

}
