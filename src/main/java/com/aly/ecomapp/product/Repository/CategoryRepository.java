package com.aly.ecomapp.product.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.aly.ecomapp.product.entity.Category;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Find by name (exact match)
    Category findByName(String name);
//    Category findById(long id);
    // Find by name containing (case-insensitive)
    List<Category> findByNameContainingIgnoreCase(String name);


    // Check if name exists (for validation)
    boolean existsByName(String name);

    void deleteById(long id);

}