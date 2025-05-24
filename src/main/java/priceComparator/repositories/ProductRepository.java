package priceComparator.repositories;

import org.springframework.data.jpa.repository.Query;
import priceComparator.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for performing CRUD operations on {@link Product} entities.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Method that fetches the most recently added {@link Product} based on productId and storeName (case-insensitive).
     * Used to calculate the discounted product by fetching the most recent product price added to the DB.
     *
     * @param productId the business id of the product.
     * @param storeName the name of the store.
     * @return the most recent {@link Product} for the given ID and store.
     */
    Optional<Product> findTopByProductIdAndStoreNameIgnoreCaseOrderByDateAddedDesc(String productId, String storeName);

    /**
     * Retrieves a list of all distinct store names found in the product table.
     * Useful when needing to evaluate prices or availability across all stores.
     *
     * @return a list of unique store name strings.
     */
    @Query("SELECT DISTINCT p.storeName FROM Product p")
    List<String> findAllDistinctStoreNames();

    /**
     * Method that retrieves all {@link Product} entries that match a name.
     * Used for showing price trends over time.
     *
     * @param productName the name of the {@link Product}.
     * @return a list of {@link Product} entries.
     */
    List<Product> findByNameIgnoreCaseOrderByDateAddedAsc(String productName);

    /**
     * Method that fetches the most recently added {@link Product} based on name and storeName (case-insensitive).
     * Used to optimize ProductList/ ShoppingCart.
     *
     * @param productName the business id of the product.
     * @param store the name of the store.
     * @return the most recent {@link Product} for the given ID and store.
     */
    Optional<Product> findTopByNameIgnoreCaseAndStoreNameIgnoreCaseOrderByDateAddedDesc(String productName, String store);
}
