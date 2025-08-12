package com.aly.ecomapp.service;
import com.aly.ecomapp.exception.ProductExceptionMessages;
import com.aly.ecomapp.exception.ProductException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.aly.ecomapp.dto.CategoryDto;
import com.aly.ecomapp.entity.Category;
import com.aly.ecomapp.repository.CategoryRepository;
import com.aly.ecomapp.repository.ProductRepository;
import com.aly.ecomapp.exception.CategoryException;
import com.aly.ecomapp.exception.CategoryExceptionMessages;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Autowired
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

        Category savedCategory;
        try {
            savedCategory = categoryRepository.save(category);
        } catch (Exception e) {
            throw new CategoryException(CategoryExceptionMessages.FAILED_TO_CREATE_CATEGORY ,e);
        }
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

        Category updatedCategory;
        try {
            updatedCategory = categoryRepository.save(existingCategory);
        } catch (Exception e) {
            throw new CategoryException(CategoryExceptionMessages.FAILED_TO_UPDATE_CATEGORY);
        }
        return convertToDto(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryException(CategoryExceptionMessages.CATEGORY_NOT_FOUND + id));

        if (productRepository.existsByCategory(category)) {
            throw new ProductException(ProductExceptionMessages.CANNOT_DELETE_DUE_TO_EXISTING_PRODUCT);
        }

        try {
            categoryRepository.delete(category);
        } catch (Exception e) {
            throw new CategoryException(CategoryExceptionMessages.FAILED_TO_DELETE_CATEGORY,e);
        }
    }

    private CategoryDto convertToDto(Category category) {
        if(category == null) {
            throw new CategoryException(CategoryExceptionMessages.CATEGORY_NOT_FOUND);
        }
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        return dto;
    }
}