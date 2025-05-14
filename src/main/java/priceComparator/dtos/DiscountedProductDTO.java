package priceComparator.dtos;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import priceComparator.models.Currency;
import priceComparator.models.PackageUnit;

/**
 * Data Transfer Object used to encapsulate and transport information about discounted products across layers.
 * This class is not persistent in the database and is intended for read-only views.
 */
@Data
@AllArgsConstructor
public class DiscountedProductDTO {

    /**
     * Name of the discounted product
     */
    private String name;

    /**
     * Brand of the discounted product.
     */
    private String brand;

    /**
     * External/business product ID this discount refers to.
     *
     */
    @Column(nullable = false)
    private String productId;

    /**
     * Product category (e.g., "lactate", "băuturi").
     */
    private String category;

    /**
     * Original price before discount.
     */
    private Double originalPrice;

    /**
     * Percentage of the discount [0-100].
     */
    private Integer discount;

    /**
     * Final price after discount.
     */
    private Double discountedPrice;

    /**
     * Currency in which the price is expressed, serialized as a string value.
     * All instances of the DTO will have the price transformed to RON for fair comparison.
     */
    @Enumerated(EnumType.STRING)
    private Currency currency;

    /**
     * Price per unit of measurement.
     */
    private Double pricePerUnit;

    /**
     * Unit of the package (e.g., kg, l, buc).
     */
    @Enumerated(EnumType.STRING)
    private PackageUnit packageUnit;

    /**
     * Store that offers the discounted product.
     */
    private String storeName;
}
