package com.aly.ecomapp.dto;
import lombok.Data;

@Data
public class CategoryDto {
    private Long id;
    private String name;
    private String slug;
    private String image;
    private String description;
}