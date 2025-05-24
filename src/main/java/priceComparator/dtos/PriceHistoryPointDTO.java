package priceComparator.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import priceComparator.models.Product;
import java.time.LocalDate;


/**
 * DTO representing a segment of a {@link Product} entity's price history.
 * Each instance captures a specific time window during which a product had a certain price,
 * optionally affected by a discount, and contextual store/brand information.
 */
@Data
@AllArgsConstructor
public class PriceHistoryPointDTO {
    /**
     * The start date of the price period.
     */
    private LocalDate dateFrom;

    /**
     * The end date of the price period.
     */
    private LocalDate dateTo;

    /**
     * The price during this time range.
     * If discounted is true, this is the discounted price.
     */
    private double price;

    /**
     * Indicates whether the price is a result of a discount.
     */
    private boolean discounted;

    /**
     * The name of the store offering this price.
     */
    private String storeName;

    /**
     * The brand of the product associated with this price segment.
     */
    private String brand;
}