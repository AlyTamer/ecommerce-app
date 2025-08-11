package com.aly.ecomapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Used for both user and admin registration */
public record RegisterRequest(
        @Email(message = "email must be valid")
        @NotBlank(message = "email is required")
        String email,

        @NotBlank(message = "password is required")
        @Size(min = 6, max = 72, message = "password must be 6-72 chars")
        String password
) {}
