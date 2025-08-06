package com.aly.ecomapp.products;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double price;
    private Integer quantity;
    private Double rating;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
enum ProductStatus {
    ACTIVE, INACTIVE, OUT_OF_STOCK, DISCONTINUED
}