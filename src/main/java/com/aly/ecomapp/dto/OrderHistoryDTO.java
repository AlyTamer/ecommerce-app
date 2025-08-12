package com.aly.ecomapp.dto;

import com.aly.ecomapp.entity.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class OrderHistoryDTO {
    private Long id;
    private Long orderId; // References the original order
    private OrderStatus status;
    private BigDecimal totalPrice;
    private LocalDateTime changedAt;
}


