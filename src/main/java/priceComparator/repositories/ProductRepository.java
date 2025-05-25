package priceComparator.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
     * Method that fetches the most recently added {@link Product} based on name and storeName (case-insensitive).
     * Used to optimize ProductList/ ShoppingCart.
     *
     * @param productName the business id of the product.
     * @param store the name of the store.
     * @return the most recent {@link Product} for the given ID and store.
     */
    Optional<Product> findTopByNameIgnoreCaseAndStoreNameIgnoreCaseOrderByDateAddedDesc(String productName, String store);

    /**
     * Retrieves and filters product entries by name, store, brand, or category.
     * Used for price history.
     *
     * @param name name of the product to analyze
     * @param store optional store filter
     * @param category optional category filter
     * @param brand optional brand filter
     * @return a list of {@link Product} objects that match the filters.
     */
    @Query("""
    SELECT p FROM Product p
    WHERE LOWER(p.name) = LOWER(:name)
      AND (:store IS NULL OR LOWER(p.storeName) = LOWER(:store))
      AND (:category IS NULL OR LOWER(p.category) = LOWER(:category))
      AND (:brand IS NULL OR LOWER(p.brand) = LOWER(:brand))
    ORDER BY p.dateAdded ASC
""")
    List<Product> findFilteredProducts(
            @Param("name") String name,
            @Param("store") String store,
            @Param("category") String category,
            @Param("brand") String brand
    );
}
