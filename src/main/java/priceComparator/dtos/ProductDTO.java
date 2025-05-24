package priceComparator.dtos;
import priceComparator.models.Product;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * Data Transfer Object used to encapsulate and transport information about current {@link Product}
 * entities across layers.
 * This class is not persistent in the database and is intended for read-only views.
 */
@Data
@Getter
@AllArgsConstructor
public class ProductDTO {

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
     * Original price before discount in RON.
     */
    private Double originalPrice;

    /**
     * Percentage of the discount [0-100].
     */
    private Integer discountPercentage;

    /**
     * Final price after discount in RON.
     */
    private Double discountedPrice;

    /**
     * Price per unit of measurement (e.g. 100 RON/kg).
     * All price per unit will be in RON, kg for mass and l for volume for fair comparison.
     */
    private String pricePerUnit;

    /**
     * Store that offers the discounted product.
     */
    private String storeName;
}
