package com.aly.ecomapp.product.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.aly.ecomapp.product.DTO.CategoryDTO;
import com.aly.ecomapp.product.entity.Category;
import com.aly.ecomapp.product.Repository.CategoryRepository;
import com.aly.ecomapp.product.Repository.ProductRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Find by name (exact match)
    Optional<Category> findByName(String name);

    // Find by name containing (case-insensitive)
    List<Category> findByNameContainingIgnoreCase(String name);


    // Check if name exists (for validation)
    boolean existsByName(String name);

}