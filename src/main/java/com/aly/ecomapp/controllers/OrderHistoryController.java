package com.aly.ecomapp.controllers;

import com.aly.ecomapp.dto.OrderHistoryDTO;
import com.aly.ecomapp.security.AllowedUser;
import com.aly.ecomapp.service.OrderHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-history")
@PreAuthorize("hasRole('ADMIN')")
public class OrderHistoryController {

    private final OrderHistoryService service;

    public OrderHistoryController(OrderHistoryService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(
            summary = "Get all order histroy for specific user",
            description = "Retrieve a list of all order history entries.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @AllowedUser
    public ResponseEntity<List<OrderHistoryDTO>> getAllOrderHistories(@RequestParam Long userId) {
        List<OrderHistoryDTO> orderHistories = service.getAllOrderHistories(userId);
        return ResponseEntity.ok(orderHistories);
    }
    @PostMapping
    @Operation(
            summary = "Create a new order history entry",
            description = "Create a new order history entry with the provided details.\n Admin only method",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<OrderHistoryDTO> create(@RequestBody OrderHistoryDTO dto) {
        OrderHistoryDTO saved = service.create(dto);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update an existing order history entry",
            description = "Update an existing order history entry with the provided details.\n Admin only method",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<OrderHistoryDTO> update(@PathVariable Long id, @RequestBody OrderHistoryDTO dto) {
        OrderHistoryDTO saved = service.update(id, dto);
        return saved == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(saved);
    }

    @GetMapping
    @Operation(
            summary = "Get all order history entries",
            description = "Retrieve a list of all order history entries.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public List<OrderHistoryDTO> all() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get order history entry by ID",
            description = "Retrieve a specific order history entry by its ID.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<OrderHistoryDTO> byId(@PathVariable Long id) {
        OrderHistoryDTO found = service.getById(id);
        return (found == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(found);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete an order history entry",
            description = "Delete a specific order history entry by its ID.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
