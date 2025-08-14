package com.aly.ecomapp.controllers;
import com.aly.ecomapp.dto.CategoryDto;
import com.aly.ecomapp.dto.CategoryRequestDto;
import com.aly.ecomapp.security.AllowedUser;
import com.aly.ecomapp.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@AllowedUser
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/category")
    @Operation(
        summary = "Get all categories",
        description = "Retrieve a list of all product categories.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get category by ID",
        description = "Retrieve a specific category by its ID.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    @Operation(
        summary = "Create a new category",
        description = "Create a new product category.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryRequestDto createDto) {
        CategoryDto createdCategory = categoryService.createCategory(createDto);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(
        summary = "Update an existing category",
        description = "Update the details of an existing product category.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<CategoryDto> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryRequestDto updateDto) {
        return ResponseEntity.ok(categoryService.updateCategory(id, updateDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete a category",
        description = "Delete a product category by its ID.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

}