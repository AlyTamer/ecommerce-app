package com.aly.ecomapp.controllers;

import com.aly.ecomapp.dto.OrderDTO;
import com.aly.ecomapp.security.AllowedUser;
import com.aly.ecomapp.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@PreAuthorize("hasRole('ADMIN')")

public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(
            summary = "Get Uuser orders",
            description = "Retrieve a list of all orders.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @AllowedUser
    public ResponseEntity<List<OrderDTO>> getAllOrdersByUserId(@RequestParam(required = true) Long userId) {
        List<OrderDTO> orders = service.getAllOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    @PostMapping
    @Operation(
            summary = "Create a new order",
            description = "Create a new order with the provided details.\n Admin only method",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<OrderDTO> create(@RequestBody OrderDTO order) {
        OrderDTO saved = service.create(order);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update an existing order",
            description = "Update an existing order with the provided details.\n Admin only method",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<OrderDTO> update(@PathVariable Long id, @RequestBody OrderDTO order) {
        OrderDTO saved = service.update(id, order);
        return saved == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(saved);
    }

    @GetMapping
    @Operation(
            summary = "Get all orders",
            description = "Retrieve a list of all orders.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public List<OrderDTO> all() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get order by ID",
            description = "Retrieve a specific order by its ID.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<OrderDTO> byId(@PathVariable Long id) {
        OrderDTO found = service.getById(id);
        return (found == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(found);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete an order",
            description = "Delete an order by its ID.\n Admin only method",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
