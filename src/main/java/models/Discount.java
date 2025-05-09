package models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productId;

    private String name;

    private String category;

    private String brand;

    private Double packageQuantity;

    @Enumerated(EnumType.STRING)
    private PackageUnit packageUnit;

    private LocalDate dateFrom;

    private LocalDate dateTo;

    @Min(0)
    @Max(100)
    private Integer percentage;
}
