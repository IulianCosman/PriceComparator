package priceComparator.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import priceComparator.dtos.PriceHistoryPointDTO;
import priceComparator.models.Discount;
import priceComparator.models.Product;
import priceComparator.repositories.DiscountRepository;
import priceComparator.repositories.ProductRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static priceComparator.utils.PriceUtils.convertToRon;
import static priceComparator.utils.PriceUtils.round;

/**
 * Service responsible for constructing the historical price timeline of a {@link Product}.
 * Allows filtering by store, brand, or category. Outputs a list of time-segmented
 * {@link PriceHistoryPointDTO} (with or without {@link Discount}) that can be visualized as a time series.
 */
@Service
public class PriceHistoryService {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    DiscountRepository discountRepository;

    /**
     * Main method to retrieve a product's price history.
     * Filters products, applies discount logic, and builds a time-series representation.
     *
     * @param productName name of the product to analyze
     * @param storeName optional store filter
     * @param category optional category filter
     * @param brand optional brand filter
     * @return a list of {@link PriceHistoryPointDTO} objects, each representing a time segment
     */
    public List<PriceHistoryPointDTO> getPriceHistory(
            String productName,
            Optional<String> storeName,
            Optional<String> category,
            Optional<String> brand
    ) {
        LocalDate today = LocalDate.now();

        // Step 1: Filter product entries based on parameters
        List<Product> filteredProducts = productRepository.findFilteredProducts(
                productName,
                storeName.orElse(null),
                category.orElse(null),
                brand.orElse(null)
        );

        if (filteredProducts.isEmpty()) return List.of();

        // Step 2: Group products by store (each store gets its own price timeline, useless if storeName is given as parameter)
        Map<String, List<Product>> groupedByStore = filteredProducts.stream()
                .collect(Collectors.groupingBy(p -> p.getStoreName().toLowerCase()));

        // Step 3: Get all matching discounts for the product name
        List<Discount> discounts = discountRepository.findByNameIgnoreCase(productName);

        // Step 4: Build the price timeline with discount segments
        return buildPriceTimeline(groupedByStore, discounts, today);

    }


    /**
     * Builds a detailed price timeline per store, including both discounted and non-discounted segments.
     *
     * @param groupedByStore map of storeName → list of {@link Product} entries (ordered by date)
     * @param discounts list of all discounts applicable to the product
     * @param today current date (used for range upper bound)
     * @return list of {@link PriceHistoryPointDTO} representing each time-price interval
     */
    private List<PriceHistoryPointDTO> buildPriceTimeline(
            Map<String, List<Product>> groupedByStore,
            List<Discount> discounts,
            LocalDate today
    ){
        List<PriceHistoryPointDTO> history = new ArrayList<>();

        // Iterate through the stores
        for (var entry : groupedByStore.entrySet()) {
            String store = entry.getKey();
            List<Product> products = entry.getValue();

            // Iterate through the product entries of this store
            for (int i = 0; i < products.size(); i++) {
                Product current = products.get(i);

                // DateFrom for this entry is set as the date it was added
                LocalDate dateFrom = current.getDateAdded();

                // DateTo for this entry is set as the date when the next entry was added
                // If this is the latest entry the dateTo is set to today
                LocalDate dateTo = (i < products.size() - 1) ? products.get(i + 1).getDateAdded() : today;

                double originalPrice = convertToRon(current.getPrice(), current.getCurrency());

                // Step 1: Get all discounts that overlap with this price period
                List<Discount> overlapping = discounts.stream()
                        .filter(d -> d.getStoreName().equalsIgnoreCase(store))
                        .filter(d -> !d.getDateTo().isBefore(dateFrom) && !d.getDateFrom().isAfter(dateTo))
                        .toList();

                if (overlapping.isEmpty()) {
                    // Step 2: If no discounts, the full segment is un-discounted
                    history.add(new PriceHistoryPointDTO(dateFrom, dateTo, round(originalPrice), false, current.getStoreName(), current.getBrand()));
                } else {
                    // Step 3: Build time segments with and without discounts
                    LocalDate segmentStart = dateFrom;
                    for (Discount d : overlapping) {

                        // First iteration:
                        // Make sure the discountStart is after the dateFrom for the product
                        // Ensures we don't apply a discount to an entry before it existed

                        // Other iterations:
                        // We use the correct discountStart
                        LocalDate discountStart = Collections.max(List.of(dateFrom, d.getDateFrom()));

                        // Same logic as above
                        LocalDate discountEnd = Collections.min(List.of(dateTo, d.getDateTo()));

                        // Skip if the discount interval is zero-length
                        if (discountStart.equals(discountEnd)) continue;

                        // If the discount is applied after some time we add a price history point
                        // For the un-discounted price in that time frame before the discount
                        if (segmentStart.isBefore(discountStart)) {
                            history.add(new PriceHistoryPointDTO(segmentStart, discountStart, round(originalPrice), false, current.getStoreName(), current.getBrand()));
                        }

                        // We add a price history point for the discounted Price
                        double discountedPrice = round(originalPrice * (1 - d.getPercentage() / 100.0));
                        history.add(new PriceHistoryPointDTO(discountStart, discountEnd, discountedPrice, true, current.getStoreName(), current.getBrand()));

                        // Move segmentStart forward
                        segmentStart = discountEnd;
                    }

                    // Step 4: Add remaining segment if any
                    if (segmentStart.isBefore(dateTo)) {
                        history.add(new PriceHistoryPointDTO(segmentStart, dateTo, round(originalPrice), false, current.getStoreName(), current.getBrand()));
                    }
                }
            }
        }

        return history;
    }
}
