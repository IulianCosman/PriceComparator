package priceComparator.services;

import priceComparator.models.Currency;
import priceComparator.models.Discount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import priceComparator.dtos.DiscountedProductDTO;
import priceComparator.repositories.DiscountRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for managing business logic related to {@link Discount}.
 * Handles fetching discounts, converting to DTOs, and calculating discount metrics.
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

    /**
     * Retrieves all currently active discounts and maps them to DTOs.
     * Only discounts whose date range includes the current date are included.
     *
     * @return a list of {@link DiscountedProductDTO} representing current discounts.
     */
    public List<DiscountedProductDTO> getAllCurrentDiscounts(){
        LocalDate now = LocalDate.now();
        List<Discount> activeDiscounts = discountRepository.findActiveDiscounts(now);
        return activeDiscounts.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the top N discounts sorted by price per unit in descending order.
     *
     * @param topN the number of top discounts to return.
     * @return a sorted list of {@link DiscountedProductDTO} by price per unit.
     */

    public List<DiscountedProductDTO> getTopDiscountsByPricePerUnit(int topN){
        return getAllCurrentDiscounts().stream()
                .sorted((d1,d2) -> d2.getPricePerUnit().compareTo(d1.getPricePerUnit()))
                .limit(topN)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the top N discounts sorted by discount percentage in descending order.
     *
     * @param topN the number of top discounts to return.
     * @return a sorted list of {@link DiscountedProductDTO} by discount percentage.
     */
    public List<DiscountedProductDTO> getTopDiscountsByDiscount(int topN){
        return getAllCurrentDiscounts().stream()
                .sorted((d1,d2) -> d2.getDiscount().compareTo(d1.getDiscount()))
                .limit(topN)
                .collect(Collectors.toList());
    }

    /**
     * Maps a {@link Discount} entity to a {@link DiscountedProductDTO}, computing
     * the discounted price, converting to RON if needed, and calculating price per unit.
     *
     * @param discount the discount entity to convert.
     * @return a fully populated DTO with calculated fields.
     */
    private DiscountedProductDTO mapToDTO(Discount discount){
        Double originalPrice = discount.getProduct().getPrice();
        double discountedPrice = originalPrice * (1 - discount.getPercentage() / 100.0);

        // Convert to RON for better comparison
        Currency currency = discount.getProduct().getCurrency();
        switch (currency) {
            case USD:
                originalPrice *= 4.6;
                discountedPrice *= 4.6;
                currency = Currency.RON;
                break;
            case EUR:
                originalPrice *= 5.0;
                discountedPrice *= 5.0;
                currency =  Currency.RON;
                break;
            case RON:
            default:
                break;
        }

        double pricePerUnit = discountedPrice / discount.getPackageQuantity();
        return new DiscountedProductDTO(
                discount.getName(),
                discount.getBrand(),
                discount.getCategory(),
                originalPrice,
                discount.getPercentage(),
                round(discountedPrice),
                currency,
                round(pricePerUnit),
                discount.getPackageUnit()
        );
    }

    /**
     * Utility method to round a double value to 2 decimal places.
     *
     * @param value the value to round.
     * @return the rounded value.
     */
    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
