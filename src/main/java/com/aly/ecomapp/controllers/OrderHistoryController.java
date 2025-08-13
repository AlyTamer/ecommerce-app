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
        OrderHistoryDTO saved = service.create(dto);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderHistoryDTO> update(@PathVariable Long id, @RequestBody OrderHistoryDTO dto) {
        OrderHistoryDTO saved = service.update(id, dto);
        return saved == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(saved);
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
