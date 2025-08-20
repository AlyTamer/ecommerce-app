package com.aly.ecomapp.controllers;

import com.aly.ecomapp.dto.*;
import com.aly.ecomapp.entity.Role;
import com.aly.ecomapp.service.AuthService;
import com.aly.ecomapp.service.PasswordService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService auth;
    private final PasswordService passwordService;

    public AuthController(AuthService auth , PasswordService passwordService) {
        this.auth = auth;
        this.passwordService = passwordService;
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

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequestDTO request) {
        String email = request.getEmail();
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email is required"));
        }
        try {
            passwordService.sendPasswordResetLink(email);
            return ResponseEntity.ok(Map.of("message", "Password reset link sent to " + email));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody ResetPasswordRequestDTO request) {
        System.out.println("Incoming reset password request: token=" + request.getToken() + ", newPassword=" + request.getNewPassword());

        if (request.getToken() == null || request.getToken().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Token is required"));
        }
        if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Password must be at least 6 characters"));
        }

        try {
            passwordService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(Map.of("message", "Password has been reset successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

}
