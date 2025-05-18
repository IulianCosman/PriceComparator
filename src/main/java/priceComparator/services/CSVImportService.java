package priceComparator.services;


import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import priceComparator.models.Currency;
import priceComparator.models.Discount;
import priceComparator.models.PackageUnit;
import priceComparator.models.Product;
import priceComparator.repositories.DiscountRepository;
import priceComparator.repositories.ProductRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.Locale;

/**
 * Service responsible for importing product and discount data from CSV files.
 * Automatically extracts store name and date from the file name.
 */
@Service
public class CSVImportService {

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Imports product data from a CSV file.
     * Expects columns in the order:
     * product_id, name, category, brand, package_quantity, package_unit, price, currency
     * @param file the uploaded CSV file
     */
    public void importProducts(MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename();
        String store = extractStoreName(filename);
        LocalDate dateAdded = extractDate(filename);

        try (CSVReader reader = new CSVReaderBuilder(
                new BufferedReader(new InputStreamReader(file.getInputStream())))
                .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                .build()) {
            String[] line;
            boolean skipHeader = true;

            while ((line = reader.readNext()) != null) {
                if (skipHeader) {
                    skipHeader = false;
                    continue;
                }

                Product product = new Product();
                product.setProductId(line[0]);
                product.setName(line[1]);
                product.setCategory(line[2]);
                product.setBrand(line[3]);
                product.setPackageQuantity(Double.parseDouble(line[4]));
                product.setPackageUnit(PackageUnit.valueOf(line[5]));
                product.setPrice(Double.parseDouble(line[6]));
                product.setCurrency(Currency.valueOf(line[7].toUpperCase(Locale.ROOT)));
                product.setStoreName(store);
                product.setDateAdded(dateAdded);

                productRepository.save(product);
            }
        }
    }

    /**
     * Imports discount data from a CSV file.
     * Expects columns in the order:
     * product_id, name, brand, package_quantity, package_unit, category, from_date, to_date, discount_percentage
     * @param file the uploaded CSV file
     */
    public void importDiscounts(MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename();
        String store = extractStoreName(filename);
        LocalDate dateAdded = extractDate(filename);

        try (CSVReader reader = new CSVReaderBuilder(
                new BufferedReader(new InputStreamReader(file.getInputStream())))
                .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                .build()) {
            String[] line;
            boolean skipHeader = true;

            while ((line = reader.readNext()) != null) {
                if (skipHeader) {
                    skipHeader = false;
                    continue;
                }

                if (line.length < 9) continue; // Basic validation

                Discount discount = new Discount();
                discount.setProductId(line[0]);
                discount.setName(line[1]);
                discount.setBrand(line[2]);
                discount.setPackageQuantity(Double.parseDouble(line[3]));
                discount.setPackageUnit(PackageUnit.valueOf(line[4]));
                discount.setCategory(line[5]);
                discount.setDateFrom(LocalDate.parse(line[6]));
                discount.setDateTo(LocalDate.parse(line[7]));
                discount.setPercentage(Integer.parseInt(line[8]));
                discount.setStoreName(store);
                discount.setDateAdded(dateAdded);

                discountRepository.save(discount);
            }
        }
    }

    /**
     * Extracts the store name from the CSV filename.
     * Example: `lidl_2025-05-01.csv` → `lidl`
     * @param filename the name of the file
     * @return the extracted store name
     */
    private String extractStoreName(String filename) {
        return filename.split("_")[0];
    }

    /**
     * Extracts the date from the CSV filename.
     * Example: `kaufland_2025-05-08.csv` → `2025-05-08`
     * @param filename the name of the file
     * @return the extracted date as LocalDate
     */
    private LocalDate extractDate(String filename) {
        try {

            String[] parts = filename.replace(".csv", "").split("_");
            String datePart;

            if (parts.length == 2) {
                // Format: store_yyyy-MM-dd
                datePart = parts[1];
            } else if (parts.length == 3) {
                // Format: store_discounts_yyyy-MM-dd
                datePart = parts[2];
            } else {
                throw new IllegalArgumentException("Filename format not supported");
            }
            return LocalDate.parse(datePart);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid filename format. Must be like 'store_yyyy-MM-dd.csv'");
        }
    }

}
