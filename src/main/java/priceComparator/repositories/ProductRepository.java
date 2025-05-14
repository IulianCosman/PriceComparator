package priceComparator.repositories;

import priceComparator.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for performing CRUD operations on {@link Product} entities.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Method that fetches a {@link Product} based on productId and name of the store.
     * Used to fetch products to apply discounts to.
     *
     * @param productId the business id of the product.
     * @param storeName the name of the store where the product is found.
     * @return the {@link Product} entity.
     */
    Optional<Product> findByProductIdAndStoreNameIgnoreCase(String productId, String storeName);

}
