package priceComparator.repositories;

import priceComparator.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for performing CRUD operations on {@link Product} entities.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
}
