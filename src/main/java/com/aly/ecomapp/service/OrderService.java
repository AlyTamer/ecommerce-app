package com.aly.ecomapp.service;

import com.aly.ecomapp.dto.OrderDTO;
import com.aly.ecomapp.entity.Order;
import com.aly.ecomapp.entity.OrderHistory;
import com.aly.ecomapp.entity.OrderStatus;
import com.aly.ecomapp.repository.OrderHistoryRepository;
import com.aly.ecomapp.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final String ORDER_NOT_FOUND = "Order not found";

    private final OrderRepository orderRepository;
    private final OrderHistoryRepository orderHistoryRepository;

    public OrderService(OrderRepository orderRepository,
                        OrderHistoryRepository orderHistoryRepository) {
        this.orderRepository = orderRepository;
        this.orderHistoryRepository = orderHistoryRepository;
    }

    // ---------- CRUD using DTOs ----------

    @Transactional
    public OrderDTO create(OrderDTO dto) {
        Order toSave = mapToEntity(dto);
        // ensure timestamps & default status
        if (toSave.getCreatedAt() == null) {
            toSave.setCreatedAt(LocalDateTime.now());
        }
        toSave.setUpdatedAt(toSave.getCreatedAt());
        if (toSave.getStatus() == null) {
            toSave.setStatus(OrderStatus.CREATED);
        }

        Order saved = orderRepository.save(toSave);

        // create a history snapshot automatically
        OrderHistory snap = new OrderHistory();
        snap.setOrder(saved);
        snap.setStatus(saved.getStatus());
        snap.setTotalPrice(saved.getTotalPrice());
        snap.setChangedAt(LocalDateTime.now());
        // if your OrderHistory has userId column, set it here:
        try {
            // reflect userId if the entity has it
            var userIdField = OrderHistory.class.getDeclaredField("userId");
            userIdField.setAccessible(true);
            userIdField.set(snap, saved.getUserId());
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
            // ignore if OrderHistory does not have userId
        }

        orderHistoryRepository.save(snap);

        return mapToDTO(saved);
    }

    @Transactional
    public OrderDTO update(Long id, OrderDTO dto) {
        Order existing = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(ORDER_NOT_FOUND));

        // Update allowed fields
        if (dto.getStatus() != null) existing.setStatus(dto.getStatus());
        if (dto.getTotalPrice() != null) existing.setTotalPrice(dto.getTotalPrice());
        if (dto.getUserId() != null) existing.setUserId(dto.getUserId());
        existing.setUpdatedAt(LocalDateTime.now());

        Order saved = orderRepository.save(existing);

        // append a history entry for the update
        OrderHistory snap = new OrderHistory();
        snap.setOrder(saved);
        snap.setStatus(saved.getStatus());
        snap.setTotalPrice(saved.getTotalPrice());
        snap.setChangedAt(LocalDateTime.now());
        try {
            var userIdField = OrderHistory.class.getDeclaredField("userId");
            userIdField.setAccessible(true);
            userIdField.set(snap, saved.getUserId());
        } catch (NoSuchFieldException | IllegalAccessException ignored) {}

        orderHistoryRepository.save(snap);

        return mapToDTO(saved);
    }

    public OrderDTO getById(Long id) {
        return orderRepository.findById(id)
                .map(this::mapToDTO)
                .orElse(null);
    }

    public List<OrderDTO> getAll() {
        return orderRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new IllegalArgumentException(ORDER_NOT_FOUND);
        }
        orderRepository.deleteById(id);
        // (Optional) keep history; if you want to delete, add repository call here.
    }

    // ---------- Mapping helpers ----------

    private OrderDTO mapToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setStatus(order.getStatus());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        return dto;
    }

    private Order mapToEntity(OrderDTO dto) {
        Order order = new Order();
        // id is generated; ignore dto.id on create
        order.setUserId(dto.getUserId());
        order.setStatus(dto.getStatus());
        order.setTotalPrice(dto.getTotalPrice());
        order.setCreatedAt(dto.getCreatedAt());
        order.setUpdatedAt(dto.getUpdatedAt());
        return order;
    }
}
