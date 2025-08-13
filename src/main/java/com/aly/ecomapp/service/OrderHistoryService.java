package com.aly.ecomapp.service;

import com.aly.ecomapp.dto.OrderHistoryDTO;
import com.aly.ecomapp.entity.Order;
import com.aly.ecomapp.entity.OrderHistory;
import com.aly.ecomapp.repository.OrderHistoryRepository;
import com.aly.ecomapp.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderHistoryService {

    private final OrderHistoryRepository historyRepository;
    private final OrderRepository orderRepository;

    public OrderHistoryService(OrderHistoryRepository historyRepository,
                               OrderRepository orderRepository) {
        this.historyRepository = historyRepository;
        this.orderRepository = orderRepository;
    }

    public OrderHistoryDTO create(OrderHistoryDTO dto) {
        OrderHistory entity = toEntity(dto);
        entity.setId(null);
        OrderHistory saved = historyRepository.save(entity);
        return toDTO(saved);
    }

    public OrderHistoryDTO update(Long id, OrderHistoryDTO dto) {
        OrderHistory history = historyRepository.findById(id).orElse(null);
        if (history == null) return null;

        if (dto.getStatus() != null) history.setStatus(dto.getStatus());
        if (dto.getTotalPrice() != null) history.setTotalPrice(dto.getTotalPrice());
        if (dto.getUserId() != null) history.setUserId(dto.getUserId());
        if (dto.getOrderId() != null && !dto.getOrderId().equals(history.getOrder().getId())) {
            Order ord = orderRepository.findById(dto.getOrderId()).orElse(null);
            if (ord != null) history.setOrder(ord);
        }

        return toDTO(historyRepository.save(history));
    }

    public OrderHistoryDTO getById(Long id) {
        return historyRepository.findById(id).map(this::toDTO).orElse(null);
    }

    public List<OrderHistoryDTO> getAll() {
        return historyRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<OrderHistoryDTO> getByOrder(Long orderId) {
        return historyRepository.findByOrderId(orderId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public void delete(Long id) {
        historyRepository.deleteById(id);
    }

    // mapping

    private OrderHistoryDTO toDTO(OrderHistory h) {
        OrderHistoryDTO dto = new OrderHistoryDTO();
        dto.setId(h.getId());
        dto.setOrderId(h.getOrder().getId());
        dto.setUserId(h.getUserId());
        dto.setStatus(h.getStatus());
        dto.setTotalPrice(h.getTotalPrice());
        dto.setChangedAt(h.getChangedAt());
        return dto;
    }

    private OrderHistory toEntity(OrderHistoryDTO dto) {
        OrderHistory h = new OrderHistory();
        h.setId(dto.getId());

        // IMPORTANT: link to Order using orderRepository (this fixes your earlier error)
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + dto.getOrderId()));
        h.setOrder(order);

        h.setUserId(dto.getUserId() != null ? dto.getUserId() : order.getUserId());
        h.setStatus(dto.getStatus());
        h.setTotalPrice(dto.getTotalPrice());
        h.setChangedAt(dto.getChangedAt());
        return h;
    }
}
