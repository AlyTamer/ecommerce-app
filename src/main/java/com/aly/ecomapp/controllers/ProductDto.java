package com.aly.ecomapp.controllers;
import com.aly.ecomapp.entity.ProductStatus;
import lombok.Data;

@Data
public class ProductDto {
    private Long id;
    private String name;
    private Double price;
    private Integer quantity;
    private Double rating;
    private ProductStatus status;
    private Long categoryId;
    private String categoryName;
}