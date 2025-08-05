package com.aly.ecomapp.exceptions;

import java.time.Instant;

public record ErrorDTO(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) { }
