package com.aly.ecomapp.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class CartItemDTO {
    private Long productId;
    private int quantity;
    //private BigDecimal price;


    public CartItemDTO() {}

    public CartItemDTO(  Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
