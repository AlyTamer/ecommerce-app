package com.aly.ecomapp.service;

import com.aly.ecomapp.dto.OrderDTO;
import com.aly.ecomapp.entity.Order;
import com.aly.ecomapp.entity.OrderHistory;
import com.aly.ecomapp.repository.OrderHistoryRepository;
import com.aly.ecomapp.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderHistoryRepository historyRepository;

    public OrderService(OrderRepository orderRepository, OrderHistoryRepository historyRepository) {
        this.orderRepository = orderRepository;
        this.historyRepository = historyRepository;
    }

    // ===== CRUD using DTOs =====

    @Transactional
    public OrderDTO create(OrderDTO dto) {
        Order entity = toEntity(dto);
        entity.setId(null); // ensure insert
        Order saved = orderRepository.save(entity);

        // automatic history row on create
        OrderHistory h = new OrderHistory();
        h.setOrder(saved);
        h.setUserId(saved.getUserId());
        h.setStatus(saved.getStatus());
        h.setTotalPrice(saved.getTotalPrice());
        historyRepository.save(h);

        return toDTO(saved);
    }

    public OrderDTO getById(Long id) {
        return orderRepository.findById(id).map(this::toDTO).orElse(null);
    }

    public List<OrderDTO> getAll() {
        return orderRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public OrderDTO update(Long id, OrderDTO dto) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) return null;

        // apply allowed updates (status/price can trigger history)
        boolean changed = false;

        if (dto.getStatus() != null && dto.getStatus() != order.getStatus()) {
            order.setStatus(dto.getStatus());
            changed = true;
        }
        if (dto.getTotalPrice() != null && (order.getTotalPrice() == null ||
                dto.getTotalPrice().compareTo(order.getTotalPrice()) != 0)) {
            order.setTotalPrice(dto.getTotalPrice());
            changed = true;
        }
        if (dto.getUserId() != null) {
            order.setUserId(dto.getUserId());
        }

        Order saved = orderRepository.save(order);

        if (changed) {
            OrderHistory h = new OrderHistory();
            h.setOrder(saved);
            h.setUserId(saved.getUserId());
            h.setStatus(saved.getStatus());
            h.setTotalPrice(saved.getTotalPrice());
            historyRepository.save(h);
        }

        return toDTO(saved);
    }

    @Transactional
    public void delete(Long id) {
        orderRepository.deleteById(id);
        // histories are removed by orphanRemoval if you remove via entity graph;
        // here we delete order only – FK/constraint handles children.
    }

    // ===== mapping =====

    private OrderDTO toDTO(Order o) {
        OrderDTO dto = new OrderDTO();
        dto.setId(o.getId());
        dto.setUserId(o.getUserId());
        dto.setStatus(o.getStatus());
        dto.setTotalPrice(o.getTotalPrice());
        dto.setCreatedAt(o.getCreatedAt());
        dto.setUpdatedAt(o.getUpdatedAt());
        return dto;
    }

    private Order toEntity(OrderDTO dto) {
        Order o = new Order();
        o.setId(dto.getId());
        o.setUserId(dto.getUserId());
        o.setStatus(dto.getStatus());
        o.setTotalPrice(dto.getTotalPrice());
        o.setCreatedAt(dto.getCreatedAt());
        o.setUpdatedAt(dto.getUpdatedAt());
        return o;
    }
}
