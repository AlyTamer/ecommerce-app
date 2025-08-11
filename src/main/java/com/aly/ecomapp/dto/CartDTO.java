package com.aly.ecomapp.dto;


import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CartDTO {

    private Long id;
    private Long userId;
    private List<CartItemDTO> items;
    private BigDecimal total;

    public CartDTO() {}

    public CartDTO(Long id, Long userId, List<CartItemDTO> items, BigDecimal total) {
        this.id = id;
        this.userId = userId;
        this.items = items;
        this.total = total;
    }

}

