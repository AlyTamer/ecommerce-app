package com.aly.ecomapp.service;

import com.aly.ecomapp.dto.OrderHistoryDTO;
import com.aly.ecomapp.entity.Order;
import com.aly.ecomapp.entity.OrderHistory;
import com.aly.ecomapp.exception.OrderHistoryException;
import com.aly.ecomapp.exception.OrderHistoryExceptionMessages;
import com.aly.ecomapp.repository.OrderHistoryRepository;
import com.aly.ecomapp.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderHistoryService {



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
                .orElseThrow(() -> new OrderHistoryException(OrderHistoryExceptionMessages.ORDER_HISTORY_NOT_FOUND));
        OrderHistory history = new OrderHistory();
        history.setOrder(order);
        history.setStatus(dto.getStatus());
        history.setTotalPrice(dto.getTotalPrice());
        history.setChangedAt(dto.getChangedAt() != null ? dto.getChangedAt() : LocalDateTime.now());


        OrderHistory saved;
        try {
            saved = orderHistoryRepository.save(history);
        } catch (Exception e) {
            throw new OrderHistoryException(OrderHistoryExceptionMessages.FAILED_TO_CREATE_ORDER_HISTROY);
        }
        return mapToDTO(saved);
    }


    public OrderHistoryDTO getById(Long id) {
        return orderHistoryRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(()-> new OrderHistoryException(OrderHistoryExceptionMessages.ORDER_HISTORY_NOT_FOUND));
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
                .orElseThrow(() -> new OrderHistoryException(OrderHistoryExceptionMessages.ORDER_HISTORY_NOT_FOUND));

        if (dto.getOrderId() != null && !dto.getOrderId().equals(history.getOrder().getId())) {
            Order order = orderRepository.findById(dto.getOrderId())
                    .orElseThrow(() -> new OrderHistoryException(OrderHistoryExceptionMessages.ORDER_HISTORY_NOT_FOUND));
            history.setOrder(order);

        }

        if (dto.getStatus() != null) history.setStatus(dto.getStatus());
        if (dto.getTotalPrice() != null) history.setTotalPrice(dto.getTotalPrice());
        history.setChangedAt(dto.getChangedAt() != null ? dto.getChangedAt() : LocalDateTime.now());

        OrderHistory saved;
        try {
            saved = orderHistoryRepository.save(history);
        } catch (Exception e) {
            throw new OrderHistoryException(OrderHistoryExceptionMessages.FAILED_TO_UPDATE);
        }
        return mapToDTO(saved);
    }

    public void delete(Long id) {
        if (!orderHistoryRepository.existsById(id)) {
            throw new OrderHistoryException(OrderHistoryExceptionMessages.ORDER_HISTORY_NOT_FOUND);
        }
        try {
            orderHistoryRepository.deleteById(id);
        } catch (Exception e) {
            throw new OrderHistoryException(OrderHistoryExceptionMessages.FAILED_TO_DELETE);
        }
    }

    private OrderHistoryDTO mapToDTO(OrderHistory history) {
        OrderHistoryDTO dto = new OrderHistoryDTO();
        dto.setId(history.getId());
        dto.setOrderId(history.getOrder() != null ? history.getOrder().getId() : null);
        dto.setStatus(history.getStatus());
        dto.setTotalPrice(history.getTotalPrice());
        dto.setChangedAt(history.getChangedAt());
        dto.setUserId(history.getUserId());
        return dto;
    }

    public List<OrderHistoryDTO> getAllOrderHistories(Long userId) {
    return orderHistoryRepository.findAllByUserId(userId)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }
}
