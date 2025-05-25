package priceComparator.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import priceComparator.dtos.PriceHistoryPointDTO;
import priceComparator.services.PriceHistoryService;
import priceComparator.models.Product;
import priceComparator.models.Discount;

import java.util.List;
import java.util.Optional;

/**
 * Rest Controller that enables a front-end to display the history of {@link Product} prices across stores, including
 * a {@link Discount} if there was one active
 */
@RestController
@RequestMapping("/priceHistory")
public class PriceHistoryController {
    @Autowired
    PriceHistoryService priceHistoryService;

    /**
     * Returns the full price history timeline of a given product name,
     * including price changes and discount intervals. Optionally filters by store, brand, or category.
     *
     * @param productName the product name to fetch history for
     * @param storeName optional store name to filter by
     * @param category optional product category
     * @param brand optional brand name
     * @return list of time-segmented price points
     */
    @GetMapping("/")
    public ResponseEntity<List<PriceHistoryPointDTO>> getPriceHistory(
            @RequestParam String productName,
            @RequestParam(required = false) String storeName,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand
    ) {
        List<PriceHistoryPointDTO> history = priceHistoryService.getPriceHistory(
                productName,
                Optional.ofNullable(storeName),
                Optional.ofNullable(category),
                Optional.ofNullable(brand)
        );
        return ResponseEntity.ok(history);
    }
}
