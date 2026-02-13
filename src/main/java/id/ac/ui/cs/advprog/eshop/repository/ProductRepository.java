package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Product;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Repository
public class ProductRepository {
    private List<Product> productData = new ArrayList<>();

    public Product create(Product product) {
        try {
            if (product == null) {
                throw new IllegalArgumentException("Product cannot be null");
            }
            if (product.getProductId() == null) {
                product.setProductId(java.util.UUID.randomUUID().toString());
            }
            productData.add(product);
            return product;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create product: " + e.getMessage());
        }
    }

    public Product update(Product product) {
        try {
            for (int i = 0; i < productData.size(); i++) {
                if (productData.get(i).getProductId().equals(product.getProductId())) {
                    productData.set(i, product);
                    return product;
                }
            }
            // Jika loop selesai tanpa return, berarti ID tidak ditemukan
            throw new IllegalArgumentException("Product with ID " + product.getProductId() + " not found");
        } catch (Exception e) {
            throw e;
        }
    }

    public void delete(String id) {
        try {
            boolean removed = productData.removeIf(product -> product.getProductId().equals(id));
            if (!removed) {
                throw new IllegalArgumentException("Product with ID " + id + " not found for deletion");
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public Product findById(String id) {
        try {
            for (Product product : productData) {
                if (product.getProductId().equals(id)) {
                    return product;
                }
            }
            throw new IllegalArgumentException("Product not found");
        } catch (Exception e) {
            return null;
        }
    }

    public Iterator<Product> findAll() {
        return productData.iterator();
    }
}