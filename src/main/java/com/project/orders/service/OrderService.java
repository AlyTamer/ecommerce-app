package com.project.orders.service;

import com.project.orders.entity.Order;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    Order create(Order order);
    Order getById(UUID id);
    List<Order> getAll();
    void delete(UUID id);
}
