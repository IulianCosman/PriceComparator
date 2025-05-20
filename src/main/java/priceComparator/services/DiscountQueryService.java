package priceComparator.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import priceComparator.dtos.DiscountedProductDTO;
import priceComparator.models.Currency;
import priceComparator.models.Discount;
import priceComparator.models.PackageUnit;
import priceComparator.models.Product;
import priceComparator.repositories.DiscountRepository;
import priceComparator.repositories.ProductRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer for managing business logic related to querying {@link Discount} and {@link Product}.
 * Handles fetching discounts, converting to DTOs, and calculating discount metrics.
 */
@Service
public class DiscountQueryService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DiscountRepository discountRepository;

    /**
     * Retrieves the discounts added today/ yesterday.
     *
     * @return a list of {@link DiscountedProductDTO} with info of the newly added discounts.
     */
    public List<DiscountedProductDTO> getNewDiscounts(){
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        List<Discount> newDiscounts = discountRepository.findByDateAddedIn(List.of(today, yesterday));

        return newDiscounts.stream()
                .map(this::mapToDTO)
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
                .sorted((d1,d2) -> d2.getDiscountPercentage().compareTo(d1.getDiscountPercentage()))
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
        // Match the product by productId and storeName
        // Make sure it's the latest price, hence the order by and top
        Optional<Product> optionalProduct = productRepository.findTopByProductIdAndStoreNameIgnoreCaseOrderByDateAddedDesc(
                discount.getProductId(), discount.getStoreName()
        );

        if (optionalProduct.isEmpty()) {
            throw new IllegalArgumentException("No matching product found for discount for " + discount.getName() +" from " + discount.getStoreName());
        }

        Product product = optionalProduct.get();
        Double originalPrice = product.getPrice();
        Currency currency = product.getCurrency();
        PackageUnit packageUnit = product.getPackageUnit();
        Double packageQuantity = product.getPackageQuantity();

        double discountedPrice = originalPrice * (1 - discount.getPercentage() / 100.0);

        // Convert to RON for better comparison
        switch (currency) {
            case USD:
                originalPrice *= 4.6;
                discountedPrice *= 4.6;
                break;
            case EUR:
                originalPrice *= 5.0;
                discountedPrice *= 5.0;
                break;
            case RON:
            default:
                break;
        }

        // Convert g to kg if needed
        if(packageUnit == PackageUnit.g){
            packageUnit = PackageUnit.kg;
            packageQuantity = packageQuantity/1000;
        }

        // Convert ml to l if needed
        if(packageUnit == PackageUnit.ml){
            packageUnit = PackageUnit.l;
            packageQuantity = packageQuantity/1000;
        }

        double pricePerUnit = discountedPrice / packageQuantity;
        return new DiscountedProductDTO(
                discount.getName(),
                discount.getBrand(),
                discount.getProductId(),
                discount.getCategory(),
                round(originalPrice),
                discount.getPercentage(),
                round(discountedPrice),
                round(pricePerUnit) + " RON per " + packageUnit.toString(),
                discount.getStoreName()
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

}
