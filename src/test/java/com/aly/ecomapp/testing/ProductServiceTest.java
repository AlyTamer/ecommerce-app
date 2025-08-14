package com.aly.ecomapp.testing;
import com.aly.ecomapp.dto.ProductDto;
import com.aly.ecomapp.dto.ProductRequestDto;
import com.aly.ecomapp.repository.CategoryRepository;
import com.aly.ecomapp.repository.ProductRepository;
import com.aly.ecomapp.service.ProductService;
import com.aly.ecomapp.entity.Category;
import com.aly.ecomapp.entity.Product;
import com.aly.ecomapp.entity.ProductStatus;

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
        ProductRequestDto dto = new ProductRequestDto();
        dto.setName("Phone");
        dto.setPrice(500.0);
        dto.setQuantity(10);
        dto.setRating(4.3);
        dto.setStatus("ACTIVE");
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
        ProductRequestDto updateDto = new ProductRequestDto();
        updateDto.setName("Updated");
        updateDto.setPrice(999.0);
        updateDto.setQuantity(20);
        updateDto.setRating(4.9);
        updateDto.setStatus("OUT_OF_STOCK");
        updateDto.setCategoryId(1L);
        Category category = new Category(1L, "Electronics", "Tech");


        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(existing);

        ProductDto result = productService.updateProduct(1L, updateDto);

        assertEquals("Updated", result.getName());
        assertEquals("OUT_OF_STOCK", result.getStatus());
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
        assertNotNull(result.getStatus());
        assertEquals("DISCONTINUED", result.getStatus());
    }
    @Test
    void searchProducts_ReturnsMatching() {
        Product product = new Product(1L, "Tablet", 300.0, 5, 4.0, electronics, ProductStatus.ACTIVE);

        when(productRepository.findByTitleContainingIgnoreCase("Tab")).thenReturn(List.of(product));

        List<ProductDto> result = productService.searchProducts("Tab");

        assertEquals(1, result.size());
        assertEquals("Tablet", result.get(0).getName());
    }
}
