package priceComparator.repositories;

import priceComparator.models.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import priceComparator.models.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for managing {@link Discount} entities.
 * Provides methods to query active discounts and search with filters.
 */
@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {

    /**
     * Finds all discounts that are currently active based on the given date.
     *
     * @param now The current date to check discount validity.
     * @return List of discounts active on the given date.
     */
    @Query("SELECT d FROM Discount d WHERE :now BETWEEN d.dateFrom AND d.dateTo")
    List<Discount> findActiveDiscounts(@Param("now")LocalDate now);

    /**
     * Returns discounts that have dateAdded in a list of given dates.
     * Used for seeing which discounts have been added today/yesterday.
     *
     * @param dates    The list of dates in which dateAdded should be.
     * @return List of matching discounts.
     */
    List<Discount> findByDateAddedIn(List<LocalDate> dates);

    /**
     * Returns a list of discounts for a given {@link Product} that existed.
     * Used to show price history.
     *
     * @param productName the name of the {@link Product}
     * @return a list of matching discounts for that {@link Product}
     */
    List<Discount> findByNameIgnoreCase(String productName);
}
