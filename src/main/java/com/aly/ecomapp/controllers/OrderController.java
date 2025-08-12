package com.aly.ecomapp.controllers;

import com.aly.ecomapp.dto.OrderDTO;
import com.aly.ecomapp.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Create an order (request + response are DTOs)
    @PostMapping
    public ResponseEntity<OrderDTO> create(@RequestBody OrderDTO order) {
        OrderDTO saved = orderService.create(order);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // Get all orders (DTO list)
    @GetMapping
    public List<OrderDTO> all() {
        return orderService.getAll();
    }

    // Get one order by id (DTO)
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> byId(@PathVariable Long id) {
        OrderDTO found = orderService.getById(id);
        if (found == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(found);
    }

    // Delete by id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
