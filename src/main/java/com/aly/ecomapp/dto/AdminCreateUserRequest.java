package com.aly.ecomapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Used by admin to create a user (or another admin). */
public record AdminCreateUserRequest(
        @Email(message = "email must be valid")
        @NotBlank(message = "email is required")
        String email,

        @NotBlank(message = "password is required")
        @Size(min = 6, max = 72, message = "password must be 6-72 chars")
        String password,

        // Optional; defaults to USER if null/blank
        String role
) {}
