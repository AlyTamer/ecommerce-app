package com.aly.ecomapp.dto;
import lombok.Data;

import java.util.List;

@Data
public class ProductDto {
    private Long id;
    private String title;
    private Double price;
    private Integer quantity;
    private Double rating;
    private String status;
    private Long categoryId;
    private String categoryName;
    private String description;
    private List<String> images;

}