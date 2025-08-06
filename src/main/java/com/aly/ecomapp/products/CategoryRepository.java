package com.aly.ecomapp.products;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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