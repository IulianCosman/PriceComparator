package priceComparator.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;

/**
 * Represents a discount associated with a product.
 * Contains information such as the discount percentage, applicable date range, and product packaging details.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Discount {

    /**
     * Unique identifier for the discount.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * External/business product ID this discount refers to.
     *
     */
    @Column(nullable = false)
    private String productId;

    /**
     * Name of the product as listed in the discount entry.
     * (Redundant but useful for CSV traceability.)
     */
    @Column(nullable = false)
    private String name;

    /**
     * Product category (e.g., "lactate", "băuturi").
     */
    @Column(nullable = false)
    private String category;

    /**
     * Brand of the product.
     */
    @Column(nullable = false)
    private String brand;

    /**
     * Quantity in the package (e.g., 1, 0.5).
     */
    @Column(nullable = false)
    private Double packageQuantity;

    /**
     * Unit of the package (e.g., kg, l, buc).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PackageUnit packageUnit;

    /**
     * Start date of the discount.
     */
    @Column(nullable = false)
    private LocalDate dateFrom;

    /**
     * End date of the discount.
     */
    @Column(nullable = false)
    private LocalDate dateTo;

    /**
     * Percentage of the discount [0-100].
     */
    @Column(nullable = false)
    @Min(0)
    @Max(100)
    private Integer percentage;

    /**
     * Store that offers the discount (e.g., "Lidl").
     */
    @Column(nullable = false)
    private String storeName;

    /**
     * Date this discount data was added (from CSV filename).
     */
    @Column(nullable = false)
    private LocalDate dateAdded;
}
