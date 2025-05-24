package priceComparator.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

/**
 * Entity representing a user-defined price alert for a product.
 * Used to notify the user when a product's price drops to or below the target.
 */
@Entity
@Data
public class PriceAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The product name this alert is set for */
    private String productName;

    /** Target price defined by the user */
    private double targetPrice;

    /** User identifier (email) */
    private String userEmail;

    /** Whether the user has already been notified */
    private boolean notified;

    /** Date the alert was created */
    private LocalDate createdAt;
}