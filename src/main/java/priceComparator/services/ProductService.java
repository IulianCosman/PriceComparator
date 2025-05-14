package priceComparator.services;

import priceComparator.models.Discount;
import priceComparator.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import priceComparator.repositories.ProductRepository;

import java.util.List;

/**
 * Service layer for managing business logic related to {@link Product}.
 * Handles fetching products, etc.
 */
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    /**
     * Retrieves all products from the database (active or not).
     *
     * @return a list of all {@link Product} entities.
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Saves a new discount.
     *
     * @param product the {@link Product} to save.
     * @return the persisted {@link Product} entity.
     */
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    /**
     * Retrieves a product by its unique identifier.
     *
     * @param id the ID of the product.
     * @return the found {@link Product} or null if not found.
     */
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }
}
