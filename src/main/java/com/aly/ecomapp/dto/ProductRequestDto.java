package com.aly.ecomapp.dto;
import lombok.Data;
@Data
public class ProductRequestDto {
    private String name;
    private Double price;
    private Integer quantity;
    private Double rating;
    private String status;
    private Long categoryId;
}