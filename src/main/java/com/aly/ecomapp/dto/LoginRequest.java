package com.aly.ecomapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** User/Admin login */
public record LoginRequest(
        @Email(message = "email must be valid")
        @NotBlank(message = "email is required")
        String email,

        @NotBlank(message = "password is required")
        String password
) {}
