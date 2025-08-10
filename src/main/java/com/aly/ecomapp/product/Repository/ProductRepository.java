package com.aly.ecomapp.product.Repository;
import com.aly.ecomapp.product.entity.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import com.aly.ecomapp.product.entity.Category; // Updated import
import com.aly.ecomapp.product.entity.ProductEntity; // Updated import
import com.aly.ecomapp.product.entity.ProductStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    // Find all active products
    List<ProductEntity> findByStatus(ProductStatus status);

    // Find products by category
    List<ProductEntity> findByCategory(Category category);

    // Find active products by category
    List<ProductEntity> findByCategoryAndStatus(Category category, ProductStatus status);

    // Search products by name
    List<ProductEntity> findByNameContainingIgnoreCase(String name);

    // Search active products by name
    List<ProductEntity> findByNameContainingIgnoreCaseAndStatus(String name, ProductStatus status);

    // Check if products exist for a category
    boolean existsByCategory(Category category);

    // Custom update for product status
    @Modifying
    @Query("UPDATE ProductEntity p SET p.status = :status WHERE p.id = :id")
    void updateProductStatus(Long id, ProductStatus status);

    // Find by ID with status check
    Optional<ProductEntity> findByIdAndStatus(Long id, ProductStatus status);
}