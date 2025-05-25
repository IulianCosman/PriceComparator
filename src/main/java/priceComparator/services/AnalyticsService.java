package priceComparator.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import priceComparator.dtos.ProductDTO;
import priceComparator.mappers.ProductMapper;
import priceComparator.models.Discount;
import priceComparator.models.Product;
import priceComparator.repositories.DiscountRepository;
import priceComparator.repositories.ProductRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service layer for managing business logic related to analytics of {@link Discount} and {@link Product}.
 * Handles fetching discounts, converting to DTOs, and calculating discount metrics.
 */
@Service
public class AnalyticsService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private ProductMapper productMapper;

    /**
     * Retrieves the discounts added today/ yesterday.
     *
     * @return a list of {@link ProductDTO} with info of the newly added discounts.
     */
    public List<ProductDTO> getNewDiscounts(){
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        List<Discount> newDiscounts = discountRepository.findByDateAddedIn(List.of(today, yesterday));

        return newDiscounts.stream()
                .map(discount -> productMapper.mapToDTOWithDiscount(
                        discount,
                        productRepository.findTopByProductIdAndStoreNameIgnoreCaseOrderByDateAddedDesc(
                                discount.getProductId(),
                                discount.getStoreName()
                        )
                ))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the top N discounts sorted by discount percentage in descending order.
     *
     * @param topN the number of top discounts to return.
     * @return a sorted list of {@link ProductDTO} by discount percentage.
     */
    public List<ProductDTO> getTopDiscountsByDiscount(int topN){
        return getAllCurrentDiscounts().stream()
                .sorted((d1,d2) -> d2.getDiscountPercentage().compareTo(d1.getDiscountPercentage()))
                .limit(topN)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all currently active discounts and maps them to DTOs.
     * Only discounts whose date range includes the current date are included.
     *
     * @return a list of {@link ProductDTO} representing current discounts.
     */
    public List<ProductDTO> getAllCurrentDiscounts(){
        LocalDate now = LocalDate.now();
        List<Discount> activeDiscounts = discountRepository.findActiveDiscounts(now);
        return activeDiscounts.stream()
                .map(discount -> productMapper.mapToDTOWithDiscount(
                        discount,
                        productRepository.findTopByProductIdAndStoreNameIgnoreCaseOrderByDateAddedDesc(
                                discount.getProductId(),
                                discount.getStoreName()
                        )
                ))
                .collect(Collectors.toList());
    }



}
