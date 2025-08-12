package com.aly.ecomapp.service;

import com.aly.ecomapp.dto.OrderDTO;
import com.aly.ecomapp.entity.Order;
import com.aly.ecomapp.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // Create an order from DTO
    public OrderDTO create(OrderDTO orderDTO) {
        Order order = mapToEntity(orderDTO);
        Order savedOrder = orderRepository.save(order);
        return mapToDTO(savedOrder);
    }

    // Get order by id as DTO
    public OrderDTO getById(Long id) {
        return orderRepository.findById(id)
                .map(this::mapToDTO)
                .orElse(null);
    }

    // Get all orders as DTOs
    public List<OrderDTO> getAll() {
        return orderRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Delete order
    public void delete(Long id) {
        orderRepository.deleteById(id);
    }

    // Map entity → DTO
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

    // Map DTO → entity
    private Order mapToEntity(OrderDTO dto) {
        Order order = new Order();
        order.setId(dto.getId());
        order.setUserId(dto.getUserId());
        order.setStatus(dto.getStatus());
        order.setTotalPrice(dto.getTotalPrice());
        order.setCreatedAt(dto.getCreatedAt());
        order.setUpdatedAt(dto.getUpdatedAt());
        return order;
    }
}
