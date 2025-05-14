package priceComparator.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @Column(name = "productId", unique = true, nullable = false)
    private String productId;

    /**
     * Name of the product.
     */
    private String name;

    /**
     * Product category (e.g., "lactate", "băuturi").
     */
    private String category;

    /**
     * Brand of the product.
     */
    private String brand;

    /**
     * Unit of the package (e.g., kg, l, buc).
     */
    @Enumerated(EnumType.STRING)
    private PackageUnit packageUnit;

    /**
     * Quantity in the package (e.g., 1, 0.5).
     */
    private String packageQuantity;

    /**
     * The current price of the product (without discount), in the original currency.
     */
    private Double price;

    /**
     * The currency in which the product price is specified.
     * Stored as a string value in the database.
     */
    @Enumerated(EnumType.STRING)
    private Currency currency;

}
