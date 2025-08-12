package com.aly.ecomapp.service;
import com.aly.ecomapp.dto.ProductDto;
import com.aly.ecomapp.entity.Category;
import com.aly.ecomapp.entity.Product;
import com.aly.ecomapp.entity.ProductStatus;
import com.aly.ecomapp.exception.CategoryException;
import com.aly.ecomapp.exception.CategoryExceptionMessages;
import com.aly.ecomapp.repository.CategoryRepository;
import com.aly.ecomapp.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.aly.ecomapp.exception.ProductException;
import com.aly.ecomapp.exception.ProductExceptionMessages;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException(ProductExceptionMessages.PRODUCT_NOT_FOUND));
        return convertToDto(product);
    }

    @Transactional
    public ProductDto createProduct(ProductDto productDto) {
        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new CategoryException(CategoryExceptionMessages.CATEGORY_NOT_FOUND));

        Product product = new Product();
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        product.setQuantity(productDto.getQuantity());
        product.setRating(productDto.getRating());
        product.setStatus(productDto.getStatus() != null ?
                productDto.getStatus() : ProductStatus.ACTIVE);
        product.setCategory(category);

        Product savedProduct;
        try {
            savedProduct = productRepository.save(product);
        } catch (Exception e) {
            throw new ProductException(ProductExceptionMessages.FAILED_TO_CREATE_PRODUCT,e);
        }
        return convertToDto(savedProduct);
    }

    @Transactional
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductException(ProductExceptionMessages.PRODUCT_NOT_FOUND));

        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new CategoryException(CategoryExceptionMessages.CATEGORY_NOT_FOUND));

        existingProduct.setName(productDto.getName());
        existingProduct.setPrice(productDto.getPrice());
        existingProduct.setQuantity(productDto.getQuantity());
        existingProduct.setRating(productDto.getRating());
        existingProduct.setStatus(productDto.getStatus());
        existingProduct.setCategory(category);

        Product updatedProduct = productRepository.save(existingProduct);
        return convertToDto(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException(ProductExceptionMessages.PRODUCT_NOT_FOUND));
        product.setStatus(ProductStatus.INACTIVE);
        productRepository.save(product);
    }

    public List<ProductDto> getProductsByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException(CategoryExceptionMessages.CATEGORY_NOT_FOUND));

        return productRepository.findByCategory(category).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<ProductDto> searchProducts(String query) {
        return productRepository.findByNameContainingIgnoreCase(query).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductDto updateProductStatus(Long id, ProductStatus status) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException(ProductExceptionMessages.PRODUCT_NOT_FOUND));
        product.setStatus(status);
        return convertToDto(productRepository.save(product));
    }

    private ProductDto convertToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setQuantity(product.getQuantity());
        dto.setRating(product.getRating());
        dto.setStatus(product.getStatus());
        dto.setCategoryId(product.getCategory().getId());
        dto.setCategoryName(product.getCategory().getName());
        return dto;
    }
}