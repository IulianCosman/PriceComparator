package priceComparator;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import priceComparator.models.Currency;
import priceComparator.models.Product;
import priceComparator.repositories.ProductRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class ProductServiceTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testAddProductToMySQL() {
        Product product = new Product();
        product.setName("Test Milk");
        product.setPrice(5.99);
        product.setCurrency(Currency.RON);

        Product saved = productRepository.save(product);

        Optional<Product> found = productRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Milk");
    }
}
