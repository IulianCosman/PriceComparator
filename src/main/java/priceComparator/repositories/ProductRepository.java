package priceComparator.repositories;

import priceComparator.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for performing CRUD operations on {@link Product} entities.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Method that fetches the most recently added {@link Product}
     * based on productId and storeName (case-insensitive).
     * Used to calculate the discounted product by fetching the most recent
     * product price added to the DB.
     *
     * @param productId the business id of the product.
     * @param storeName the name of the store.
     * @return the most recent {@link Product} for the given ID and store.
     */
    Optional<Product> findTopByProductIdAndStoreNameIgnoreCaseOrderByDateAddedDesc(String productId, String storeName);


}
