package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ProductRepositoryTest {

    @InjectMocks
    ProductRepository productRepository;
    @BeforeEach
    void setUp(){
    }
    @Test
    void testCreateAndFind() {
        Product product = new Product();
        product.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        product.setProductName("Sampo Cap Bambang");
        product.setProductQuantity(100);
        productRepository.create(product);

        Iterator<Product> productIterator = productRepository.findAll();
        assertTrue(productIterator.hasNext());
        Product savedProduct = productIterator.next();
        assertEquals(product.getProductId(), savedProduct.getProductId());
        assertEquals(product.getProductName(), savedProduct.getProductName());
        assertEquals(product.getProductQuantity(), savedProduct.getProductQuantity());
    }

    @Test
    void testFindAllIfEmpty() {
        Iterator<Product> productIterator = productRepository.findAll();
        assertFalse(productIterator.hasNext());
    }

    @Test
    void testFindAllIfMoreThanOneProduct() {
        Product product1 = new Product();
        product1.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        product1.setProductName("Sampo Cap Bambang");
        product1.setProductQuantity(100);
        productRepository.create(product1);

        Product product2 = new Product();
        product2.setProductId("a0f9de46-90b1-437d-a0bf-d0821dde9096");
        product2.setProductName("Sampo Cap Usep");
        product2.setProductQuantity(50);
        productRepository.create(product2);

        Iterator<Product> productIterator = productRepository.findAll();
        assertTrue(productIterator.hasNext());
        Product savedProduct = productIterator.next();
        assertEquals(product1.getProductId(), savedProduct.getProductId());
        savedProduct = productIterator.next();
        assertEquals(product2.getProductId(), savedProduct.getProductId());
        assertFalse(productIterator.hasNext());
    }

    @Test
    void testEditProductSuccess() {
        Product product = new Product();
        product.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        product.setProductName("Sampo Lama");
        product.setProductQuantity(100);

        productRepository.create(product);

        Product updatedProduct = new Product();
        updatedProduct.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        updatedProduct.setProductName("Sampo Baru");
        updatedProduct.setProductQuantity(50);

        Product result = productRepository.update(updatedProduct);

        assertNotNull(result);
        assertEquals("Sampo Baru", result.getProductName());
        assertEquals(50, result.getProductQuantity());

        Product savedProduct = productRepository.findById("eb558e9f-1c39-460e-8860-71af6af63bd6");

        assertEquals("Sampo Baru", savedProduct.getProductName());
    }


    @Test
    void testEditProductFail() {
        Product product = new Product();
        product.setProductId("id-asli");
        product.setProductName("Barang Asli");
        productRepository.create(product);

        Product nonExistentProduct = new Product();
        nonExistentProduct.setProductId("id-palsu");
        nonExistentProduct.setProductName("Hantu");

        assertThrows(IllegalArgumentException.class, () -> {
            productRepository.update(nonExistentProduct);
        });
    }


    @Test
    void testDeleteProductSuccess() {
        Product product = new Product();
        product.setProductId("id-akan-dihapus");
        product.setProductName("Barang Sementara");

        productRepository.create(product);

        assertNotNull(productRepository.findById("id-akan-dihapus"));

        productRepository.delete("id-akan-dihapus");

        Product result = productRepository.findById("id-akan-dihapus");
        assertNull(result);

        Iterator<Product> iterator = productRepository.findAll();
        assertFalse(iterator.hasNext());
    }

    @Test
    void testDeleteProductFail() {
        Product product = new Product();
        product.setProductId("id-aman");
        product.setProductName("Barang Aman");
        productRepository.create(product);


        assertThrows(IllegalArgumentException.class, () -> {
            productRepository.delete("id-tidak-ada");
        });

        Product result = productRepository.findById("id-aman");
        assertNotNull(result);
        assertEquals("Barang Aman", result.getProductName());
    }
}
