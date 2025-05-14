package priceComparator.repositories;

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
     * Searches for currently active discounts matching optional filters.
     * If a filter is null, it is ignored in the query.
     *
     * @param now      The current date to check discount validity.
     * @param name     Optional product name filter (partial match, case-insensitive).
     * @param brand    Optional brand filter (exact match, case-insensitive).
     * @param category Optional category filter (exact match, case-insensitive).
     * @return List of matching active discounts.
     */
    @Query("SELECT d FROM Discount d WHERE :now BETWEEN d.dateFrom AND d.dateTo " +
            "AND (:name  IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%' , :name, '%')))" +
            "AND (:brand IS NULL OR LOWER(d.brand) = LOWER(:brand))" +
            "AND (:category IS NULL OR LOWER(d.category) = LOWER(:category))")
    List<Discount> searchActiveDiscounts(@Param("now") LocalDate now,@Param("name") String name, @Param("brand") String brand, @Param("category") String category);

}
