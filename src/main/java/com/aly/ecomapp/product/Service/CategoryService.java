package com.aly.ecomapp.product.Service;
import com.aly.ecomapp.product.exception.ProductExceptionMessages;
import com.aly.ecomapp.product.exception.ProductException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.aly.ecomapp.product.dto.CategoryDto;
import com.aly.ecomapp.product.entity.Category;
import com.aly.ecomapp.product.Repository.CategoryRepository;
import com.aly.ecomapp.product.Repository.ProductRepository;
import com.aly.ecomapp.product.exception.ProductExceptionMessages;
import com.aly.ecomapp.product.exception.ProductException;
import com.aly.ecomapp.product.exception.CategoryException;
import com.aly.ecomapp.product.exception.CategoryExceptionMessages;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository,
                           ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public List<CategoryDto> getAllCategories() {
        List<CategoryDto> categories = new ArrayList<>();
        List<Category> allCategories=categoryRepository.findAll();
        for(Category c : allCategories) {
            CategoryDto categoryDTO = new CategoryDto();
            categoryDTO.setId(c.getId());
            categoryDTO.setName(c.getName());
            categoryDTO.setDescription(c.getDescription());
            categories.add(categoryDTO);
        }
        return  categories;
    }

    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryException(CategoryExceptionMessages.CATEGORY_NOT_FOUND + id));
        return convertToDto(category);
    }

    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new CategoryException(CategoryExceptionMessages.CATEGORY_NAME_EXISTS + categoryDto.getName());
        }

        Category category = new Category();
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());

        Category savedCategory = categoryRepository.save(category);
        return convertToDto(savedCategory);
    }

    @Transactional
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryException(CategoryExceptionMessages.CATEGORY_NOT_FOUND + id));

        if (!existingCategory.getName().equals(categoryDto.getName()) &&
                categoryRepository.existsByName(categoryDto.getName())) {
            throw new CategoryException(CategoryExceptionMessages.CATEGORY_NAME_EXISTS + categoryDto.getName());
        }

        existingCategory.setName(categoryDto.getName());
        existingCategory.setDescription(categoryDto.getDescription());

        Category updatedCategory = categoryRepository.save(existingCategory);
        return convertToDto(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryException(CategoryExceptionMessages.CATEGORY_NOT_FOUND + id));

        if (productRepository.existsByCategory(category)) {
            throw new ProductException(ProductExceptionMessages.CANNOT_DELETE_DUE_TO_EXISTING_PRODUCT);
        }

        categoryRepository.delete(category);
    }

    private CategoryDto convertToDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        return dto;
    }
}