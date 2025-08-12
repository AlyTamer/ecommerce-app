package com.aly.ecomapp.service;

import com.aly.ecomapp.dto.CategoryDto;
import com.aly.ecomapp.dto.ProductDto;
import com.aly.ecomapp.entity.Category;
import com.aly.ecomapp.entity.Product;
import com.aly.ecomapp.entity.ProductStatus;
import com.aly.ecomapp.exception.CategoryException;
import com.aly.ecomapp.exception.CategoryExceptionMessages;
import com.aly.ecomapp.exception.ProductException;
import com.aly.ecomapp.exception.ProductExceptionMessages;
import com.aly.ecomapp.repository.CategoryRepository;
import com.aly.ecomapp.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        if (productDto.getCategory() == null || productDto.getCategory().getId() == null) {
            throw new CategoryException(CategoryExceptionMessages.CATEGORY_NOT_FOUND);
        }
        Category category = categoryRepository.findById(productDto.getCategory().getId())
                .orElseThrow(() -> new CategoryException(CategoryExceptionMessages.CATEGORY_NOT_FOUND));

        Product product = new Product();
        product.setTitle(productDto.getTitle());
        product.setSlug(productDto.getSlug());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setQuantity(productDto.getQuantity());
        product.setRating(productDto.getRating());
        product.setStatus(productDto.getStatus() != null ? productDto.getStatus() : ProductStatus.ACTIVE);
        product.setCategory(category);
        product.setImages(productDto.getImages());

        return convertToDto(productRepository.save(product));
    }


    @Transactional
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ProductException(ProductExceptionMessages.PRODUCT_NOT_FOUND));

        if (productDto.getCategory() == null || productDto.getCategory().getId() == null) {
            throw new CategoryException(CategoryExceptionMessages.CATEGORY_NOT_FOUND);
        }
        Category category = categoryRepository.findById(productDto.getCategory().getId())
                .orElseThrow(() -> new CategoryException(CategoryExceptionMessages.CATEGORY_NOT_FOUND));

        existing.setTitle(productDto.getTitle());
        existing.setSlug(productDto.getSlug());
        existing.setDescription(productDto.getDescription());
        existing.setPrice(productDto.getPrice());
        existing.setQuantity(productDto.getQuantity());
        existing.setRating(productDto.getRating());
        existing.setStatus(productDto.getStatus());
        existing.setCategory(category);
        existing.setImages(productDto.getImages());

        return convertToDto(productRepository.save(existing));
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

    private ProductDto convertToDto(Product p) {
        ProductDto dto = new ProductDto();
        dto.setId(p.getId());
        dto.setTitle(p.getTitle());
        dto.setSlug(p.getSlug());
        dto.setPrice(p.getPrice());
        dto.setDescription(p.getDescription());
        dto.setQuantity(p.getQuantity());
        dto.setRating(p.getRating());
        dto.setStatus(p.getStatus());
        dto.setImages(p.getImages()); // already a List<String> in entity

        if (p.getCategory() != null) {
            CategoryDto catDto = new CategoryDto();
            catDto.setId(p.getCategory().getId());
            catDto.setName(p.getCategory().getName());
            catDto.setDescription(p.getCategory().getDescription());
            dto.setCategory(catDto);
        }
        return dto;
    }

    public List<ProductDto> getAllFilteredProducts(String title, Integer catId, Integer priceMin, Integer priceMax) {
        List<Product> product = productRepository.findAllByCondition(title, catId, priceMin, priceMax);
        return product.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
