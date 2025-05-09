package priceComparator.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productId;

    private String name;

    private String category;

    private String brand;

    @Enumerated(EnumType.STRING)
    private PackageUnit packageQuantity;

    private String packageUnit;

    private Double price;

    @Enumerated(EnumType.STRING)
    private Currency currency;

}
