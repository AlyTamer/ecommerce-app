package com.aly.ecomapp.controllers;

import com.aly.ecomapp.dto.OrderHistoryDTO;
import com.aly.ecomapp.service.OrderHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-history")
public class OrderHistoryController {

    private final OrderHistoryService service;

    public OrderHistoryController(OrderHistoryService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<OrderHistoryDTO> create(@RequestBody OrderHistoryDTO dto) {
        return new ResponseEntity<>(service.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderHistoryDTO> update(@PathVariable Long id, @RequestBody OrderHistoryDTO dto) {
        OrderHistoryDTO saved = service.update(id, dto);
        return (saved == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(saved);
    }

    @GetMapping
    public List<OrderHistoryDTO> all() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderHistoryDTO> byId(@PathVariable Long id) {
        OrderHistoryDTO found = service.getById(id);
        return (found == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(found);
    }

    @GetMapping("/order/{orderId}")
    public List<OrderHistoryDTO> byOrder(@PathVariable Long orderId) {
        return service.getByOrder(orderId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
