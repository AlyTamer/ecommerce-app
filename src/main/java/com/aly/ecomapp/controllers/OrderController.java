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

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<OrderDTO> create(@RequestBody OrderDTO order) {
        OrderDTO saved = service.create(order);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping
    public List<OrderDTO> all() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> byId(@PathVariable Long id) {
        OrderDTO found = service.getById(id);
        return (found == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(found);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDTO> update(@PathVariable Long id, @RequestBody OrderDTO order) {
        OrderDTO saved = service.update(id, order);
        return (saved == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
