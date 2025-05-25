package priceComparator.utils;

import priceComparator.models.Currency;
import priceComparator.models.PackageUnit;

public class PriceUtils {
    /**
     * Converts the given price to RON using fixed exchange rates for USD and EUR.
     * Assumes that RON values do not require conversion.
     * Used for fair price per unit comparison.
     *
     * @param price    the original price.
     * @param currency the currency in which the price is expressed.
     * @return the equivalent price in RON.
     */
    public static double convertToRon(double price, Currency currency) {
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
    public static double normalizeQuantity(PackageUnit unit, double quantity) {
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
    public static PackageUnit normalizeUnit(PackageUnit unit) {
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
    public static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
