package com.aly.ecomapp.dto;

import jakarta.validation.constraints.NotBlank;

/** Used by PATCH /users/{id}/status */
public record UpdateStatusRequest(
        @NotBlank(message = "status is required") String status
        // Expecting "NONE" or "BLOCKED" (validated in service)
) {}
