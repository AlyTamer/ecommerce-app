package com.aly.ecomapp.dto;

/** Returned after successful login */
public record AuthResponse(
        String token,  // dummy for now
        String role    // "ADMIN" or "USER"
) {}
