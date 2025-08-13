package com.aly.ecomapp.service;

import com.aly.ecomapp.dto.OrderHistoryDTO;
import com.aly.ecomapp.entity.Order;
import com.aly.ecomapp.entity.OrderHistory;
import com.aly.ecomapp.repository.OrderHistoryRepository;
import com.aly.ecomapp.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderHistoryService {

    private static final String ORDER_HISTORY_NOT_FOUND = "Order history not found";
    private static final String ORDER_NOT_FOUND = "Order not found";

    private final OrderHistoryRepository orderHistoryRepository;
    private final OrderRepository orderRepository;

    public OrderHistoryService(OrderHistoryRepository orderHistoryRepository,
                               OrderRepository orderRepository) {
        this.orderHistoryRepository = orderHistoryRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderHistoryDTO create(OrderHistoryDTO dto) {
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException(ORDER_NOT_FOUND));

        OrderHistory history = new OrderHistory();
        history.setOrder(order);
        history.setStatus(dto.getStatus());
        history.setTotalPrice(dto.getTotalPrice());
        history.setChangedAt(dto.getChangedAt() != null ? dto.getChangedAt() : LocalDateTime.now());

        // if your OrderHistory entity has userId column:
        try {
            var userIdField = OrderHistory.class.getDeclaredField("userId");
            userIdField.setAccessible(true);
            userIdField.set(history, order.getUserId());
        } catch (NoSuchFieldException | IllegalAccessException ignored) {}

        OrderHistory saved = orderHistoryRepository.save(history);
        return mapToDTO(saved);
    }

    public OrderHistoryDTO getById(Long id) {
        return orderHistoryRepository.findById(id)
                .map(this::mapToDTO)
                .orElse(null);
    }

    public List<OrderHistoryDTO> getAll() {
        return orderHistoryRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderHistoryDTO update(Long id, OrderHistoryDTO dto) {
        OrderHistory history = orderHistoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(ORDER_HISTORY_NOT_FOUND));

        // allow changing status/price/time and (optionally) re-point to another order
        if (dto.getOrderId() != null && !dto.getOrderId().equals(history.getOrder().getId())) {
            Order order = orderRepository.findById(dto.getOrderId())
                    .orElseThrow(() -> new IllegalArgumentException(ORDER_NOT_FOUND));
            history.setOrder(order);
            try {
                var userIdField = OrderHistory.class.getDeclaredField("userId");
                userIdField.setAccessible(true);
                userIdField.set(history, order.getUserId());
            } catch (NoSuchFieldException | IllegalAccessException ignored) {}
        }

        if (dto.getStatus() != null) history.setStatus(dto.getStatus());
        if (dto.getTotalPrice() != null) history.setTotalPrice(dto.getTotalPrice());
        history.setChangedAt(dto.getChangedAt() != null ? dto.getChangedAt() : LocalDateTime.now());

        OrderHistory saved = orderHistoryRepository.save(history);
        return mapToDTO(saved);
    }

    public void delete(Long id) {
        if (!orderHistoryRepository.existsById(id)) {
            throw new IllegalArgumentException(ORDER_HISTORY_NOT_FOUND);
        }
        orderHistoryRepository.deleteById(id);
    }

    private OrderHistoryDTO mapToDTO(OrderHistory history) {
        OrderHistoryDTO dto = new OrderHistoryDTO();
        dto.setId(history.getId());
        dto.setOrderId(history.getOrder() != null ? history.getOrder().getId() : null);
        dto.setStatus(history.getStatus());
        dto.setTotalPrice(history.getTotalPrice());
        dto.setChangedAt(history.getChangedAt());
        return dto;
    }
}
