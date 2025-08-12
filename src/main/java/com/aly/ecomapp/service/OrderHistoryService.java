package com.aly.ecomapp.service;

import com.aly.ecomapp.dto.OrderHistoryDTO;
import com.aly.ecomapp.entity.OrderHistory;
import com.aly.ecomapp.exception.OrderHistoryException;
import com.aly.ecomapp.exception.OrderHistoryExceptionMessages;
import com.aly.ecomapp.repository.OrderHistoryRepository;
import com.aly.ecomapp.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderHistoryService {
    private final OrderRepository orderRepository;
    private final OrderHistoryRepository orderHistoryRepository;

    public OrderHistoryService(OrderHistoryRepository orderHistoryRepository, OrderRepository orderRepository) {
        this.orderHistoryRepository = orderHistoryRepository;
        this.orderRepository = orderRepository;
    }

    public OrderHistoryDTO create(OrderHistoryDTO dto) {
        OrderHistory history = mapToEntity(dto);
        OrderHistory saved = null;
        try {
            saved = orderHistoryRepository.save(history);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    public void delete(Long id) {
        try {
            orderHistoryRepository.deleteById(id);
        } catch (Exception e) {
            throw new OrderHistoryException(OrderHistoryExceptionMessages.FAILED_TO_DELETE);
        }
    }

    private OrderHistoryDTO mapToDTO(OrderHistory history) {
        OrderHistoryDTO dto = new OrderHistoryDTO();
        dto.setId(history.getId());
        dto.setOrderId(history.getOrder().getId());
        dto.setStatus(history.getStatus());
        dto.setTotalPrice(history.getTotalPrice());
        dto.setChangedAt(history.getChangedAt());

        return dto;
    }

    private OrderHistory mapToEntity(OrderHistoryDTO dto) {
        OrderHistory history = new OrderHistory();
        history.setId(dto.getId());
        history.setOrder(orderRepository.findById(dto.getOrderId()).orElseThrow(()-> new OrderHistoryException(OrderHistoryExceptionMessages.FAILED_TO_CREATE_ORDER_HISTROY)));
        history.setStatus(dto.getStatus());
        history.setTotalPrice(dto.getTotalPrice());
        history.setChangedAt(dto.getChangedAt());

        return history;
    }
}

