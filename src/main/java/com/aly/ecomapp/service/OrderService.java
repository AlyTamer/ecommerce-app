package com.aly.ecomapp.service;

import com.aly.ecomapp.dto.OrderDTO;
import com.aly.ecomapp.entity.Order;
import com.aly.ecomapp.entity.OrderHistory;
import com.aly.ecomapp.entity.OrderStatus;
import com.aly.ecomapp.exception.OrderException;
import com.aly.ecomapp.exception.OrderExceptionMessages;
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
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderHistoryRepository orderHistoryRepository;

    public OrderService(OrderRepository orderRepository,
                        OrderHistoryRepository orderHistoryRepository) {
        this.orderRepository = orderRepository;
        this.orderHistoryRepository = orderHistoryRepository;
    }

    @Transactional
    public OrderDTO create(OrderDTO dto) {
        Order toSave = mapToEntity(dto);
        if (toSave.getCreatedAt() == null) {
            toSave.setCreatedAt(LocalDateTime.now());
        }
        toSave.setUpdatedAt(toSave.getCreatedAt());
        if (toSave.getStatus() == null) {
            toSave.setStatus(OrderStatus.CREATED);
        }

        Order saved;
        try {
            saved = orderRepository.save(toSave);
        } catch (Exception e) {
            throw new OrderException(OrderExceptionMessages.ORDER_CREATION_FAILED);
        }

        OrderHistory snap = new OrderHistory();
        snap.setOrder(saved);
        snap.setStatus(saved.getStatus());
        snap.setTotalPrice(saved.getTotalPrice());
        snap.setChangedAt(LocalDateTime.now());
//        try {
//            var userIdField = OrderHistory.class.getDeclaredField("userId");
//            userIdField.setAccessible(true);
//            userIdField.set(snap, saved.getUserId());
//        } catch (Exception e) {
//            throw new OrderException(OrderExceptionMessages.ORDER_CREATION_FAILED);
//        }

        try {
            orderHistoryRepository.save(snap);
        } catch (Exception e) {
            throw new OrderException(OrderExceptionMessages.ORDER_HISTORY_CREATION_FAILED);
        }

        return mapToDTO(saved);
    }

    @Transactional
    public OrderDTO update(Long id, OrderDTO dto) {
        Order existing = orderRepository.findById(id)
                .orElseThrow(() -> new OrderException(OrderExceptionMessages.ORDER_NOT_FOUND));

        if (dto.getStatus() != null) existing.setStatus(dto.getStatus());
        if (dto.getTotalPrice() != null) existing.setTotalPrice(dto.getTotalPrice());
        if (dto.getUserId() != null) existing.setUserId(dto.getUserId());
        existing.setUpdatedAt(LocalDateTime.now());

        Order saved = null;
        try {
            saved = orderRepository.save(existing);
        } catch (Exception e) {
            throw new OrderException(OrderExceptionMessages.ORDER_UPDATE_FAILED);
        }

        OrderHistory snap = new OrderHistory();
        snap.setOrder(saved);
        snap.setStatus(saved.getStatus());
        snap.setTotalPrice(saved.getTotalPrice());
        snap.setChangedAt(LocalDateTime.now());

//        try {
//            var userIdField = OrderHistory.class.getDeclaredField("userId");
//            userIdField.setAccessible(true);
//            userIdField.set(snap, saved.getUserId());
//        } catch (Exception e) {
//            throw new OrderException(OrderExceptionMessages.ORDER_UPDATE_FAILED);
//        }

        try {
            orderHistoryRepository.save(snap);
        } catch (Exception e) {
            throw new OrderHistoryException(OrderHistoryExceptionMessages.FAILED_TO_CREATE_ORDER_HISTROY);
        }

        return mapToDTO(saved);
    }

    public OrderDTO getById(Long id) {
        return orderRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(()-> new OrderException(OrderExceptionMessages.ORDER_NOT_FOUND));
    }

    public List<OrderDTO> getAll() {
        return orderRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new OrderException(OrderExceptionMessages.ORDER_NOT_FOUND);
        }
        try {
            orderRepository.deleteById(id);
        } catch (Exception e) {
            throw new OrderException(OrderExceptionMessages.ORDER_DELETION_FAILED);
        }
    }


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
        order.setUserId(dto.getUserId());
        order.setStatus(dto.getStatus());
        order.setTotalPrice(dto.getTotalPrice());
        order.setCreatedAt(dto.getCreatedAt());
        order.setUpdatedAt(dto.getUpdatedAt());
        return order;
    }

    public List<OrderDTO> getAllOrdersByUserId(Long userId) {
        return orderRepository.findAllByUserId(userId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
}
