package com.aly.ecomapp.controllers;

import com.aly.ecomapp.dto.AuthResponse;
import com.aly.ecomapp.dto.LoginRequest;
import com.aly.ecomapp.dto.RegisterRequest;
import com.aly.ecomapp.dto.UserResponse;
import com.aly.ecomapp.entity.Role;
import com.aly.ecomapp.service.AuthService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(auth.register(req, Role.USER));
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(auth.login(req));
    }
}
