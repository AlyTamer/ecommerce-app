package com.aly.ecomapp.product.DTO;
import com.aly.ecomapp.product.entity.ProductStatus;
import lombok.Data;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private Double price;
    private Integer quantity;
    private Double rating;
    private ProductStatus status;
    private Long categoryId;
    private String categoryName;
}