package priceComparator.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Product class
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "productId", unique = true, nullable = false)
    private String productId;

    private String name;

    private String category;

    private String brand;

    @Enumerated(EnumType.STRING)
    private PackageUnit packageUnit;

    private String packageQuantity;

    private Double price;

    @Enumerated(EnumType.STRING)
    private Currency currency;

}
