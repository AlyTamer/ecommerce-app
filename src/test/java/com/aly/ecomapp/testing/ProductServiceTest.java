package com.aly.ecomapp.testing;
import com.aly.ecomapp.product.DTO.ProductDTO;
import com.aly.ecomapp.product.Repository.CategoryRepository;
import com.aly.ecomapp.product.Repository.ProductRepository;
import com.aly.ecomapp.product.Service.ProductService;
import com.aly.ecomapp.product.entity.Category;
import com.aly.ecomapp.product.entity.ProductEntity;
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
        ProductEntity product = new ProductEntity(1L, "Laptop", 1000.0, 5, 4.5, electronics, ProductStatus.ACTIVE);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductDTO dto = productService.getProductById(1L);

        assertEquals("Laptop", dto.getName());
        assertEquals(4.5, dto.getRating());
    }

    @Test
    void createProduct_ValidData_ReturnsSavedDTO() {
        ProductDTO dto = new ProductDTO();
        dto.setName("Phone");
        dto.setPrice(500.0);
        dto.setQuantity(10);
        dto.setRating(4.3);
        dto.setCategoryId(1L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(electronics));
        when(productRepository.save(any(ProductEntity.class)))
                .thenAnswer(inv -> {
                    ProductEntity p = inv.getArgument(0);
                    p.setId(1L);
                    return p;
                });

        ProductDTO result = productService.createProduct(dto);

        assertEquals("Phone", result.getName());
        assertEquals("Electronics", result.getCategoryName());
    }

    @Test
    void updateProduct_ChangesData() {
        ProductEntity existing = new ProductEntity(1L, "Old", 200.0, 1, 3.5, electronics, ProductStatus.ACTIVE);
        ProductDTO update = new ProductDTO();
        update.setName("Updated");
        update.setPrice(999.0);
        update.setQuantity(20);
        update.setRating(4.9);
        update.setStatus(ProductStatus.OUT_OF_STOCK);
        update.setCategoryId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(electronics));
        when(productRepository.save(any(ProductEntity.class))).thenReturn(existing);

        ProductDTO result = productService.updateProduct(1L, update);

        assertEquals("Updated", result.getName());
        assertEquals(ProductStatus.OUT_OF_STOCK, update.getStatus());
    }

    @Test
    void updateProductStatus_UpdatesCorrectly() {
        ProductEntity product = new ProductEntity(1L, "Item", 100.0, 5, 4.0, electronics, ProductStatus.ACTIVE);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(ProductEntity.class))).thenReturn(product);

        ProductDTO result = productService.updateProductStatus(1L, ProductStatus.DISCONTINUED);

        assertEquals(ProductStatus.DISCONTINUED, result.getStatus());
    }

    @Test
    void searchProducts_ReturnsMatching() {
        ProductEntity product = new ProductEntity(1L, "Tablet", 300.0, 5, 4.0, electronics, ProductStatus.ACTIVE);

        when(productRepository.findByNameContainingIgnoreCase("Tab")).thenReturn(List.of(product));

        List<ProductDTO> result = productService.searchProducts("Tab");

        assertEquals(1, result.size());
        assertEquals("Tablet", result.get(0).getName());
    }
}
