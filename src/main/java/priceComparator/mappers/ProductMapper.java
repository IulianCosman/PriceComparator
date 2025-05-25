package priceComparator.mappers;

import org.springframework.stereotype.Component;
import priceComparator.dtos.ProductDTO;
import priceComparator.models.Currency;
import priceComparator.models.Discount;
import priceComparator.models.PackageUnit;
import priceComparator.models.Product;

import java.util.Optional;

import static priceComparator.utils.PriceUtils.*;


@Component
public class ProductMapper {

    /**
     * Maps a {@link Discount} entity to a {@link ProductDTO}, computing
     * the discounted price, converting to RON if needed, and calculating price per unit.
     *
     * @param discount the discount entity to convert.
     * @return a fully populated DTO with calculated fields.
     */
    public ProductDTO mapToDTOWithDiscount(Discount discount, Optional<Product> optionalProduct) {

        if (optionalProduct.isEmpty()) {
            throw new IllegalArgumentException("No matching product found for discount for "
                    + discount.getName() + " from " + discount.getStoreName());
        }

        Product product = optionalProduct.get();

        double originalPrice = product.getPrice();
        double discountedPrice = originalPrice * (1 - discount.getPercentage() / 100.0);
        Currency currency = product.getCurrency();

        originalPrice = convertToRon(originalPrice, currency);
        discountedPrice = convertToRon(discountedPrice, currency);

        PackageUnit unit = normalizeUnit(product.getPackageUnit());
        double quantity = normalizeQuantity(product.getPackageUnit(), product.getPackageQuantity());

        double pricePerUnit = discountedPrice / quantity;

        return new ProductDTO(
                discount.getName(),
                discount.getBrand(),
                discount.getProductId(),
                discount.getCategory(),
                round(originalPrice),
                discount.getPercentage(),
                round(discountedPrice),
                round(pricePerUnit) + " RON per " + unit,
                discount.getStoreName()
        );
    }

    /**
     * Maps a {@link Product} entity to a {@link ProductDTO}
     * converting to RON if needed, and calculating price per unit.
     *
     * @param product the product entity to convert.
     * @return a fully populated DTO with calculated fields.
     */
    public ProductDTO mapToDTOWithoutDiscount(Product product) {
        double originalPrice = convertToRon(product.getPrice(), product.getCurrency());
        PackageUnit unit = normalizeUnit(product.getPackageUnit());
        double quantity = normalizeQuantity(product.getPackageUnit(), product.getPackageQuantity());
        double pricePerUnit = originalPrice / quantity;

        return new ProductDTO(
                product.getName(),
                product.getBrand(),
                product.getProductId(),
                product.getCategory(),
                round(originalPrice),
                0,
                round(originalPrice),
                round(pricePerUnit) + " RON per " + unit,
                product.getStoreName()
        );
    }


}
