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

    private final OrderHistoryService orderHistoryService;

    public OrderHistoryController(OrderHistoryService orderHistoryService) {
        this.orderHistoryService = orderHistoryService;
    }

    @PostMapping
    public ResponseEntity<OrderHistoryDTO> create(@RequestBody OrderHistoryDTO dto) {
        OrderHistoryDTO saved = orderHistoryService.create(dto);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping
    public List<OrderHistoryDTO> all() {
        return orderHistoryService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderHistoryDTO> byId(@PathVariable Long id) {
        OrderHistoryDTO found = orderHistoryService.getById(id);
        if (found == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(found);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderHistoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
