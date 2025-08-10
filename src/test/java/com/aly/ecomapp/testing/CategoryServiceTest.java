package com.aly.ecomapp.testing;

import com.aly.ecomapp.product.DTO.CategoryDTO;
import com.aly.ecomapp.product.Repository.CategoryRepository;
import com.aly.ecomapp.product.Repository.ProductRepository;
import com.aly.ecomapp.product.entity.Category;
import com.aly.ecomapp.product.Service.CategoryService;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CategoryService categoryService;

    public CategoryServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCategories() {
        List<Category> categories = Arrays.asList(
                new Category(1L, "Electronics", "Tech stuff"),
                new Category(2L, "Books", "Reading materials")
        );

        when(categoryRepository.findAll()).thenReturn(categories);

        List<CategoryDTO> result = categoryService.getAllCategories();

        assertEquals(2, result.size());
        assertEquals("Books", result.get(1).getName());
    }
    @Test
    void testUpdateCategory_Success() {
        Category existing = new Category(1L, "OldName", "OldDesc");

        CategoryDTO update = new CategoryDTO();
        update.setName("NewName");
        update.setDescription("Updated");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.existsByName("NewName")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        CategoryDTO result = categoryService.updateCategory(1L, update);

        assertEquals("NewName", result.getName());
        assertEquals("Updated", result.getDescription());
    }
    @Test
    void testCreateCategory_Success() {
        CategoryDTO dto = new CategoryDTO();
        dto.setName("Clothing");
        dto.setDescription("Fashion & apparel");

        when(categoryRepository.existsByName("Clothing")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> {
            Category cat = inv.getArgument(0);
            cat.setId(10L);
            return cat;
        });

        CategoryDTO result = categoryService.createCategory(dto);

        assertEquals("Clothing", result.getName());
        assertEquals("Fashion & apparel", result.getDescription());
    }

    @Test
    void testGetCategoryById() {
        Category category = new Category(1L, "Electronics", "Gadgets and devices");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        CategoryDTO result = categoryService.getCategoryById(1L);

        assertEquals("Electronics", result.getName());
        assertEquals("Gadgets and devices", result.getDescription());
    }
    @Test
    void testDeleteCategory_Success() {
        Category category = new Category(1L, "Sports", "All sports items");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.existsByCategory(category)).thenReturn(false);

        assertDoesNotThrow(() -> categoryService.deleteCategory(1L));
        verify(categoryRepository, times(1)).delete(category);
    }
}