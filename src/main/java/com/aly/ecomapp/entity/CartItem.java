package com.aly.ecomapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //DB auto increment ids
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;
    private int quantity;

    @Column(name = "price", nullable = false)
    private BigDecimal price;



}
