package com.aly.ecomapp.repository;
import com.aly.ecomapp.entity.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import com.aly.ecomapp.entity.Category; // Updated import
import com.aly.ecomapp.entity.Product; // Updated import
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find all active products
    List<Product> findByStatus(ProductStatus status);

    // Find products by category
    List<Product> findByCategory(Category category);

    // Find active products by category
    List<Product> findByCategoryAndStatus(Category category, ProductStatus status);

    // Search products by name
    List<Product> findByNameContainingIgnoreCase(String name);

    // Search active products by name
    List<Product> findByNameContainingIgnoreCaseAndStatus(String name, ProductStatus status);

    // Check if products exist for a category
    boolean existsByCategory(Category category);

    // Custom update for product status
    @Modifying
    @Query("UPDATE Product p SET p.status = :status WHERE p.id = :id")
    void updateProductStatus(Long id, ProductStatus status);

    // Find by ID with status check
    Optional<Product> findByIdAndStatus(Long id, ProductStatus status);
    void deleteById(Long id);
}