package priceComparator.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import priceComparator.models.PriceAlert;

import java.util.List;

/**
 * Repository interface for accessing and managing {@link PriceAlert} entities.
 */
public interface PriceAlertRepository extends JpaRepository<PriceAlert, Long> {

    /**
     * Fetches all alerts that have not yet triggered a user notification.
     */
    List<PriceAlert> findByNotifiedFalse();
}