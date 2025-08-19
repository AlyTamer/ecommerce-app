package com.aly.ecomapp.controllers;


import com.aly.ecomapp.dto.AdminCreateUserRequest;
import com.aly.ecomapp.dto.RegisterRequest;
import com.aly.ecomapp.dto.UpdateStatusRequest;
import com.aly.ecomapp.dto.UserResponse;
import com.aly.ecomapp.entity.Role;
import com.aly.ecomapp.service.AuthService;
import com.aly.ecomapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AuthService authService;
    private final UserService userService;

    public AdminUserController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @Operation(
        summary = "List all users",
        description = "Returns a list of all registered users in the system.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public List<UserResponse> list() {
        return userService.listAll();
    }

    @Operation(
        summary = "Get user by ID",
        description = "Returns details of a user by their ID.",
        security = @SecurityRequirement(name="bearerAuth")
    )
    @GetMapping("/{id}")
    public UserResponse get(@PathVariable Long id) {
        return userService.get(id);
    }

    @Operation(
        summary = "Create a new user",
        description = "Creates a new user with the specified email, password, and role (optional). " +
                      "If no role is specified, the default is USER.",
        security = @SecurityRequirement(name="bearerAuth")
    )
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody AdminCreateUserRequest req) {
        if (req.role() == null || req.role().isBlank()) {
            throw new IllegalArgumentException("Role is not provided");
        }

        Role role;
        try {
            role = Role.valueOf(req.role().trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("role must be ADMIN or USER");
        }

        var created = authService.register(
                new RegisterRequest(req.email(), req.password()),
                role
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    @Operation(
        summary = "Update user status",
        description = "Updates the status of a user by their ID. " +
                      "Valid statuses are AVAILABLE, BLOCKED.",
        security = @SecurityRequirement(name="bearerAuth")
    )
    @PatchMapping("/{id}/status")
    public UserResponse setStatus(@PathVariable Long id,
                                  @Valid @RequestBody UpdateStatusRequest req) {
        return userService.updateStatus(id, req.status());
    }

    @Operation(
        summary = "Delete a user",
        description = "Deletes a user from the system by their ID. " +
                      "This action is irreversible.",
        security = @SecurityRequirement(name="bearerAuth")
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.delete(id);
    }
}


