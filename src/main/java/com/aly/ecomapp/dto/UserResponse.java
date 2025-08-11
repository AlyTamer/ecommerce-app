package com.aly.ecomapp.dto;

/** Public-safe view of a user */
public record UserResponse(
        Long id,
        String email,
        String role,   // "ADMIN" or "USER"
        String status  // "NONE" or "BLOCKED"
) {}
