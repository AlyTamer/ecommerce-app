package com.aly.ecomapp.dto;

import com.aly.ecomapp.entity.ProductStatus;
import lombok.Data;

import java.util.List;

@Data
public class ProductDto {
    private Long id;
    private String title;
    private String slug;
    private Double price;
    private String description;
    private Integer quantity;
    private Double rating;
    private ProductStatus status;
    private CategoryDto category;
    private List<String> images;
}
