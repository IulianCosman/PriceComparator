package priceComparator.models;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;

// Data structure used to transfer data between layers
// Used to display the best discounts
@Data
@AllArgsConstructor
public class DiscountedProductDTO {
    private String name;
    private String brand;
    private String category;
    private Double originalPrice;
    private Integer discount;
    private Double discountedPrice;
    @Enumerated(EnumType.STRING)
    private Currency currency;
    private Double pricePerUnit;
    @Enumerated(EnumType.STRING)
    private PackageUnit packageUnit;
}
