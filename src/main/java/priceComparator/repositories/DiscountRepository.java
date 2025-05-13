package priceComparator.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import priceComparator.models.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {

    @Query("SELECT d FROM Discount d WHERE :now BETWEEN d.dateFrom AND D.dateTo")
    List<Discount> findActiveDiscounts(@Param("now")LocalDate now);

    @Query("SELECT d FROM Discount d WHERE :now BETWEEN d.dateFrom AND d.dateTo " +
            "AND (:name  IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%' , :name, '%')))" +
            "AND (:brand IS NULL OR LOWER(d.brand) = LOWER(:brand))" +
            "AND (:category IS NULL OR LOWER(d.category) = LOWER(:category))")
    List<Discount> searchActiveDiscounts(@Param("now") LocalDate now,@Param("name") String name, @Param("brand") String brand, @Param("category") String category);

}
