package com.aly.ecomapp.controllers;

import com.aly.ecomapp.dto.ProductDto;
import com.aly.ecomapp.dto.ProductRequestDto;
import com.aly.ecomapp.entity.ProductStatus;
import com.aly.ecomapp.security.AllowedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.aly.ecomapp.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/products")
@AllowedUser
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get all products",
            description = "Retrieve a list of all products.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get product by ID",
            description = "Retrieve a specific product by its ID.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(
            summary = "Create a new product",
            description = "Create a new product in the system.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductRequestDto createDto) {
        return ResponseEntity.ok(productService.createProduct(createDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(
            summary = "Update an existing product",
            description = "Update the details of an existing product.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductRequestDto createDto) {
        return ResponseEntity.ok(productService.updateProduct(id, createDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a product",
            description = "Delete a product from the system by its ID.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/category/{categoryId}")
    @Operation(
            summary = "Get products by category",
            description = "Retrieve a list of products belonging to a specific category.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<List<ProductDto>> getProductsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId));
    }

    @GetMapping("/search")
    @Operation(
            summary = "Search products",
            description = "Search for products by name.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<List<ProductDto>> searchProducts(@RequestParam String query) {
        return ResponseEntity.ok(productService.searchProducts(query));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    @Operation(
            summary = "Update product status",
            description = "Update the status of a product (e.g., active, inactive).",
            security = @SecurityRequirement(name = "bearerAuth")
    )


    public ResponseEntity<ProductDto> updateProductStatus(
            @PathVariable Long id,
            @RequestParam ProductStatus status) {
        return ResponseEntity.ok(productService.updateProductStatus(id, status));
    }
    @Operation(
            summary = "Get all products",
            description = "Retrieve a list of all products.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/filter")
    public ResponseEntity<List<ProductDto>> getAllProductsOnCondition(@RequestParam(required = false) String title,
                                                                      @RequestParam(required =false) Integer categoryId ,
                                                                      @RequestParam(required = false) Integer price_min,
                                                                      @RequestParam(required = false) Integer price_max) {
        return ResponseEntity.ok(productService.getAllFilteredProducts(title,categoryId,price_min,price_max));
    }
}