package com.aly.ecomapp.service;

import com.aly.ecomapp.dto.ProductDto;
import com.aly.ecomapp.dto.ProductRequestDto;
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
    public ProductDto createProduct(ProductRequestDto createDto) {
        if (createDto.getName() == null ) {
            throw new ProductException(ProductExceptionMessages.MISSING_PARAMETERS );
        }
        if (createDto.getPrice() == null || createDto.getPrice() <= 0) {
            throw new ProductException(ProductExceptionMessages.MISSING_PARAMETERS );
        }
        if (createDto.getQuantity() == null || createDto.getQuantity() < 0) {
            throw new ProductException(ProductExceptionMessages.MISSING_PARAMETERS );
        }
        if (createDto.getCategoryId() == null) {
            throw new ProductException(ProductExceptionMessages.MISSING_PARAMETERS );
        }
        if (createDto.getRating() == null) {
            throw new ProductException(ProductExceptionMessages.MISSING_PARAMETERS );
        }
        if (createDto.getStatus() == null) {
            throw new ProductException(ProductExceptionMessages.MISSING_PARAMETERS );
        }


        Category category = categoryRepository.findById(createDto.getCategoryId())
                .orElseThrow(() -> new CategoryException(CategoryExceptionMessages.CATEGORY_NOT_FOUND));

        Product product = new Product();
        product.setTitle(createDto.getName());
        product.setPrice(createDto.getPrice());
        product.setQuantity(createDto.getQuantity());
        product.setRating(createDto.getRating());
        switch (createDto.getStatus().toUpperCase()) {
            case "INACTIVE":
                product.setStatus(ProductStatus.INACTIVE);
                break;
            case "OUT_OF_STOCK":
                product.setStatus(ProductStatus.OUT_OF_STOCK);
                break;
            case "DISCONTINUED":
                product.setStatus(ProductStatus.DISCONTINUED);
                break;
            default:
                product.setStatus(ProductStatus.ACTIVE);
                break;
        }
        product.setCategory(category);

        Product savedProduct;
        try {
            savedProduct = productRepository.save(product);
        } catch (Exception e) {
            throw new ProductException(ProductExceptionMessages.FAILED_TO_CREATE_PRODUCT, e);
        }
        return convertToDto(savedProduct);
    }

    @Transactional
    public ProductDto updateProduct(Long id, ProductRequestDto updateDto) {

        if (updateDto.getName() == null ) {
            throw new ProductException(ProductExceptionMessages.MISSING_PARAMETERS );
        }
        if (updateDto.getPrice() == null || updateDto.getPrice() <= 0) {
            throw new ProductException(ProductExceptionMessages.MISSING_PARAMETERS );
        }
        if (updateDto.getQuantity() == null || updateDto.getQuantity() < 0) {
            throw new ProductException(ProductExceptionMessages.MISSING_PARAMETERS );
        }
        if (updateDto.getCategoryId() == null) {
            throw new ProductException(ProductExceptionMessages.MISSING_PARAMETERS );
        }
        if (updateDto.getRating() == null) {
            throw new ProductException(ProductExceptionMessages.MISSING_PARAMETERS );
        }
        if (updateDto.getStatus() == null) {
            throw new ProductException(ProductExceptionMessages.MISSING_PARAMETERS );
        }


        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductException(ProductExceptionMessages.PRODUCT_NOT_FOUND));

        Category category = categoryRepository.findById(updateDto.getCategoryId())
                .orElseThrow(() -> new CategoryException(CategoryExceptionMessages.CATEGORY_NOT_FOUND));

        existingProduct.setTitle(updateDto.getName());
        existingProduct.setPrice(updateDto.getPrice());
        existingProduct.setQuantity(updateDto.getQuantity());
        existingProduct.setRating(updateDto.getRating());
        switch (updateDto.getStatus().toUpperCase()) {
            case "INACTIVE":
                existingProduct.setStatus(ProductStatus.INACTIVE);
                break;
            case "OUT_OF_STOCK":
                existingProduct.setStatus(ProductStatus.OUT_OF_STOCK);
                break;
            case "DISCONTINUED":
                existingProduct.setStatus(ProductStatus.DISCONTINUED);
                break;
            default:
                existingProduct.setStatus(ProductStatus.ACTIVE);
                break;
        }
        existingProduct.setCategory(category);

        Product updatedProduct = productRepository.save(existingProduct);
        return convertToDto(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException(ProductExceptionMessages.PRODUCT_NOT_FOUND));
        product.setStatus(ProductStatus.INACTIVE);
        productRepository.delete(product);
    }

    public List<ProductDto> getProductsByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException(CategoryExceptionMessages.CATEGORY_NOT_FOUND));

        return productRepository.findByCategory(category).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<ProductDto> searchProducts(String query) {
        return productRepository.findByTitleContainingIgnoreCase(query).stream()
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
        dto.setTitle(product.getTitle());
        dto.setPrice(product.getPrice());
        dto.setQuantity(product.getQuantity());
        dto.setRating(product.getRating());
        if (product.getStatus() != null) {
            switch (product.getStatus().toString().toUpperCase()) {
                case "INACTIVE":
                    dto.setStatus(ProductStatus.INACTIVE.toString());
                    break;
                case "OUT_OF_STOCK":
                    dto.setStatus(ProductStatus.OUT_OF_STOCK.toString());
                    break;
                case "DISCONTINUED":
                    dto.setStatus(ProductStatus.DISCONTINUED.toString());
                    break;
                default:
                    dto.setStatus(ProductStatus.ACTIVE.toString());
            }
        } else {
            dto.setStatus(ProductStatus.ACTIVE.toString()); // Default value
        }

        dto.setCategoryId(product.getCategory().getId());
        dto.setCategoryName(product.getCategory().getName());
        dto.setDescription(product.getDescription());
        dto.setImages(product.getImages());
        return dto;
    }

    public List<ProductDto> getAllFilteredProducts(String title, Long catId, Double priceMin, Double priceMax) {
    return productRepository.findAllByCondition(title, catId, priceMin, priceMax).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
}