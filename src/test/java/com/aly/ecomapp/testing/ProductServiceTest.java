package com.aly.ecomapp.testing;
import com.aly.ecomapp.product.dto.ProductDto;
import com.aly.ecomapp.product.Repository.CategoryRepository;
import com.aly.ecomapp.product.Repository.ProductRepository;
import com.aly.ecomapp.product.Service.ProductService;
import com.aly.ecomapp.product.entity.Category;
import com.aly.ecomapp.product.entity.Product;
import com.aly.ecomapp.product.entity.ProductStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private Category electronics;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        electronics = new Category(1L, "Electronics", "Tech");

    }

    @Test
    void getProductById_ValidId_ReturnsProductDTO() {
        Product product = new Product(1L, "Laptop", 1000.0, 5, 4.5, electronics, ProductStatus.ACTIVE);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductDto dto = productService.getProductById(1L);

        assertEquals("Laptop", dto.getName());
        assertEquals(4.5, dto.getRating());
    }

    @Test
    void createProduct_ValidData_ReturnsSavedDTO() {
        ProductDto dto = new ProductDto();
        dto.setName("Phone");
        dto.setPrice(500.0);
        dto.setQuantity(10);
        dto.setRating(4.3);
        dto.setCategoryId(1L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(electronics));
        when(productRepository.save(any(Product.class)))
                .thenAnswer(inv -> {
                    Product p = inv.getArgument(0);
                    p.setId(1L);
                    return p;
                });

        ProductDto result = productService.createProduct(dto);

        assertEquals("Phone", result.getName());
        assertEquals("Electronics", result.getCategoryName());
    }

    @Test
    void updateProduct_ChangesData() {
        Product existing = new Product(1L, "Old", 200.0, 1, 3.5, electronics, ProductStatus.ACTIVE);
        ProductDto update = new ProductDto();
        update.setName("Updated");
        update.setPrice(999.0);
        update.setQuantity(20);
        update.setRating(4.9);
        update.setStatus(ProductStatus.OUT_OF_STOCK);
        update.setCategoryId(1L);
        Category category = new Category(1L, "Electronics", "Tech");


        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(existing);

        ProductDto result = productService.updateProduct(1L, update);

        assertEquals("Updated", result.getName());
        assertEquals(ProductStatus.OUT_OF_STOCK, update.getStatus());
    }

    @Test
    void updateProductStatus_UpdatesCorrectly() {
        Product product = Product.builder()
                .id(1L)
                .name("Item")
                .price(100.0)
                .quantity(5)
                .rating(4.0)
                .category(electronics)
                .status(ProductStatus.ACTIVE)
                .build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            return p;
        });

        ProductDto result = productService.updateProductStatus(1L, ProductStatus.DISCONTINUED);

        assertEquals(ProductStatus.DISCONTINUED, result.getStatus());
    }
    @Test
    void searchProducts_ReturnsMatching() {
        Product product = new Product(1L, "Tablet", 300.0, 5, 4.0, electronics, ProductStatus.ACTIVE);

        when(productRepository.findByNameContainingIgnoreCase("Tab")).thenReturn(List.of(product));

        List<ProductDto> result = productService.searchProducts("Tab");

        assertEquals(1, result.size());
        assertEquals("Tablet", result.get(0).getName());
    }
}
