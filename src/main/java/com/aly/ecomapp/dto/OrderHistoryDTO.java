package com.aly.ecomapp.dto;

import com.aly.ecomapp.entity.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderHistoryDTO {
    private Long id;
    private Long orderId;
    private Long userId;
    private OrderStatus status;
    private BigDecimal totalPrice;
    private LocalDateTime changedAt;
}



