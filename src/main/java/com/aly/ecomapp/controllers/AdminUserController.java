package com.aly.ecomapp.controllers;


import com.aly.ecomapp.dto.AdminCreateUserRequest;
import com.aly.ecomapp.dto.RegisterRequest;
import com.aly.ecomapp.dto.UpdateStatusRequest;
import com.aly.ecomapp.dto.UserResponse;
import com.aly.ecomapp.entity.Role;
import com.aly.ecomapp.service.AuthService;
import com.aly.ecomapp.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
@RolesAllowed("ADMIN")

public class AdminUserController {

    private final AuthService authService;
    private final UserService userService;

    public AdminUserController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }


    @GetMapping
    public List<UserResponse> list() {
        return userService.listAll();
    }


    @GetMapping("/{id}")
    public UserResponse get(@PathVariable Long id) {
        return userService.get(id);
    }

    /** CREATE user (or admin if role="ADMIN") */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody AdminCreateUserRequest req) {
        Role role = Role.USER;
        if (req.role() != null && !req.role().isBlank()) {
            try { role = Role.valueOf(req.role().trim().toUpperCase()); }
            catch (IllegalArgumentException e) { throw new IllegalArgumentException("role must be ADMIN or USER"); }
        }
        var created = authService.register(
                new RegisterRequest(req.email(), req.password()),
                role
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** UPDATE status: NONE or BLOCKED */
    @PatchMapping("/{id}/status")
    public UserResponse setStatus(@PathVariable Long id,
                                  @Valid @RequestBody UpdateStatusRequest req) {
        return userService.updateStatus(id, req.status());
    }

    /** DELETE user */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.delete(id);
    }
}


