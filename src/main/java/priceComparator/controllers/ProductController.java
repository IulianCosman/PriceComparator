package priceComparator.controllers;

import priceComparator.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import priceComparator.services.ProductService;

import java.util.List;

/**
 * REST controller that handles HTTP requests related to {@link Product} entities.
 */
@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * Retrieves all products from the database.
     *
     * @return a list of all {@link Product} entities.
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(){
        return ResponseEntity.ok(productService.getAllProducts());
    }

    /**
     * Creates a new product in the database.
     *
     * @param product the product data from the request body.
     * @return the saved {@link Product} entity.
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product){
        return ResponseEntity.ok(productService.saveProduct(product));
    }

    /**
     * Retrieves a specific product by its database ID.
     *
     * @param id the primary key of the product.
     * @return a {@link ResponseEntity} containing the product if found, or 404 Not Found if not.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id){
        Product product = productService.getProductById(id);
        if(product != null){
            return ResponseEntity.ok(product);
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }
}
