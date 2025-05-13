package priceComparator.models;

/**
 * Enum representing the various units in which a product can be packaged.
 * These units are used to describe the quantity or volume of products,
 * facilitating price comparison and standardization across different products.
 */
public enum PackageUnit {
    buc,
    l,
    g,
    kg,
    role,
    ml,
    // Fallback case
    UNKNOWN

}
