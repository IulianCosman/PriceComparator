package priceComparator.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import priceComparator.models.Product;
import priceComparator.models.Discount;
import priceComparator.services.CSVImportService;

/**
 * REST controller that handles the creation of {@link Product} and {@link Discount} entities.
 */
@RestController
@RequestMapping("/import")
public class CSVImportController{

    @Autowired
    private CSVImportService csvImportService;

    /**
     * Endpoint to import product data from a CSV file.
     * Example: POST /import/products
     */
    @PostMapping("/products")
    public ResponseEntity<String> importProducts(@RequestParam("file") MultipartFile file) {
        try {
            csvImportService.importProducts(file);
            return ResponseEntity.ok("Product CSV imported successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to import products: " + e.getMessage());
        }
    }

    /**
     * Endpoint to import discount data from a CSV file.
     * Example: POST /import/discounts
     */
    @PostMapping("/discounts")
    public ResponseEntity<String> importDiscounts(@RequestParam("file") MultipartFile file) {
        try {
            csvImportService.importDiscounts(file);
            return ResponseEntity.ok("Discount CSV imported successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to import discounts: " + e.getMessage());
        }
    }

}