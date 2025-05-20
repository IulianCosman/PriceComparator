package priceComparator.mappers;

import org.springframework.stereotype.Component;
import priceComparator.dtos.ProductDTO;
import priceComparator.models.Currency;
import priceComparator.models.Discount;
import priceComparator.models.PackageUnit;
import priceComparator.models.Product;

import java.util.Optional;

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

    /**
     * Converts the given price to RON using fixed exchange rates for USD and EUR.
     * Assumes that RON values do not require conversion.
     * Used for fair price per unit comparison.
     *
     * @param price    the original price.
     * @param currency the currency in which the price is expressed.
     * @return the equivalent price in RON.
     */
    private double convertToRon(double price, Currency currency) {
        return switch (currency) {
            case USD -> price * 4.6;
            case EUR -> price * 5.0;
            default -> price;
        };
    }

    /**
     * Normalizes the quantity to base units (kg or l) for proper price-per-unit calculation.
     * Converts grams (g) and milliliters (ml) to kilograms and liters respectively.
     *
     * @param unit     the current packaging unit.
     * @param quantity the quantity in the current unit.
     * @return the quantity converted to base unit if applicable.
     */
    private double normalizeQuantity(PackageUnit unit, double quantity) {
        if (unit == PackageUnit.g || unit == PackageUnit.ml) {
            return quantity / 1000;
        }
        return quantity;
    }

    /**
     * Converts units like grams (g) and milliliters (ml) to their normalized
     * counterparts: kilograms (kg) and liters (l). Other units remain unchanged.
     *
     * @param unit the original packaging unit.
     * @return the normalized packaging unit.
     */
    private PackageUnit normalizeUnit(PackageUnit unit) {
        if (unit == PackageUnit.g) return PackageUnit.kg;
        if (unit == PackageUnit.ml) return PackageUnit.l;
        return unit;
    }

    /**
     * Utility method to round a double value to 2 decimal places.
     *
     * @param value the value to round.
     * @return the rounded value.
     */
    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

}
