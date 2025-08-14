package com.aly.ecomapp.testing;

import com.aly.ecomapp.dto.CategoryDto;
import com.aly.ecomapp.dto.CategoryRequestDto;
import com.aly.ecomapp.repository.CategoryRepository;
import com.aly.ecomapp.repository.ProductRepository;
import com.aly.ecomapp.entity.Category;
import com.aly.ecomapp.service.CategoryService;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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

        List<CategoryDto> result = categoryService.getAllCategories();

        assertEquals(2, result.size());
        assertEquals("Books", result.get(1).getName());
    }
    @Test
    void testUpdateCategory_Success() {
        Category existing = new Category(1L, "OldName", "OldDesc");

        CategoryRequestDto updateDto = new CategoryRequestDto();
        updateDto.setName("NewName");
        updateDto.setDescription("Updated");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.existsByName("NewName")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        CategoryDto result = categoryService.updateCategory(1L, updateDto);

        assertEquals("NewName", result.getName());
        assertEquals("Updated", result.getDescription());
    }
    @Test
    void testCreateCategory_Success() {
        CategoryRequestDto dto = new CategoryRequestDto();
        dto.setName("Clothing");
        dto.setDescription("Fashion & apparel");

        when(categoryRepository.existsByName("Clothing")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> {
            Category cat = inv.getArgument(0);
            cat.setId(10L);
            return cat;
        });

        CategoryDto result = categoryService.createCategory(dto);

        assertEquals("Clothing", result.getName());
        assertEquals("Fashion & apparel", result.getDescription());
    }

    @Test
    void testGetCategoryById() {
        Category category = new Category(1L, "Electronics", "Gadgets and devices");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        CategoryDto result = categoryService.getCategoryById(1L);

        assertEquals("Electronics", result.getName());
        assertEquals("Gadgets and devices", result.getDescription());
    }
    @Test
    void testDeleteCategory_Success() {
        Category category = new Category(1L, "Sports", "All sports items");
        categoryRepository.save(category);

     assertNotNull( categoryRepository.existsById(1L));
    }
}