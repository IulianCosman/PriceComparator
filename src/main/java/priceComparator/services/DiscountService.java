package priceComparator.services;

import priceComparator.models.Currency;
import priceComparator.models.Discount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import priceComparator.models.DiscountedProductDTO;
import priceComparator.repositories.DiscountRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<DiscountedProductDTO> getAllCurrentDiscounts(){
        LocalDate now = LocalDate.now();
        List<Discount> activeDiscounts = discountRepository.findActiveDiscounts(now);
        return activeDiscounts.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<DiscountedProductDTO> getTopDiscountsByPricePerUnit(int topN){
        return getAllCurrentDiscounts().stream()
                .sorted((d1,d2) -> d2.getPricePerUnit().compareTo(d1.getPricePerUnit()))
                .limit(topN)
                .collect(Collectors.toList());
    }

    public List<DiscountedProductDTO> getTopDiscountsByDiscount(int topN){
        return getAllCurrentDiscounts().stream()
                .sorted((d1,d2) -> d2.getDiscount().compareTo(d1.getDiscount()))
                .limit(topN)
                .collect(Collectors.toList());
    }

    // Returns a DTO with the original price, the discounted one, the price per unit
    // Converts all prices to RON for fair comparison
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

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
