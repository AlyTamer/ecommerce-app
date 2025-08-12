package com.aly.ecomapp.security;

import org.springframework.security.access.prepost.PreAuthorize;


@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public @interface AllowedUser {
}
