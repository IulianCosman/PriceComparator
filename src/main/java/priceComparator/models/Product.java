package priceComparator.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Represents a product.
 * Contains information such as price, brand, etc.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    /**
     * Unique identifier for the product.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Business identifier of the product, used to uniquely distinguish products across data sources.
     * This is not the same as the database-generated ID.
     */
    @Column(nullable = false)
    private String productId;

    /**
     * Name of the product.
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
     * Unit of the package (e.g., kg, l, buc).
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PackageUnit packageUnit;

    /**
     * Quantity in the package (e.g., 1, 0.5).
     */
    @Column(nullable = false)
    private String packageQuantity;

    /**
     * The current price of the product (without discount), in the original currency.
     */
    @Column(nullable = false)
    private Double price;

    /**
     * The currency in which the product price is specified.
     * Stored as a string value in the database.
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Currency currency;

    /**
     * Name of the store (e.g., "Kaufland", "Lidl").
     */
    @Column(nullable = false)
    private String storeName;

    /**
     * Date when the product was added to the database (parsed from CSV filename).
     */
    @Column(nullable = false)
    private LocalDate dateAdded;
}
