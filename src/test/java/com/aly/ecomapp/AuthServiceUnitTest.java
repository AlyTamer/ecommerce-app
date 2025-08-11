package com.aly.ecomapp;

import com.aly.ecomapp.Entity.AppUser;
import com.aly.ecomapp.Entity.Role;
import com.aly.ecomapp.Entity.UserStatus;
import com.aly.ecomapp.dto.AuthResponse;
import com.aly.ecomapp.dto.LoginRequest;
import com.aly.ecomapp.dto.RegisterRequest;
import com.aly.ecomapp.dto.UserResponse;
import com.aly.ecomapp.repository.AppUserRepository;
import com.aly.ecomapp.security.JwtUtil;
import com.aly.ecomapp.service.AuthService;
import com.aly.ecomapp.service.PasswordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceUnitTest {

    @Mock AppUserRepository repo;
    @Mock PasswordService passwordService;
    @Mock JwtUtil jwt;
    @InjectMocks AuthService authService;

    @Test
    void register_success_thenDuplicateFails() {
        when(repo.existsByEmail("user1@example.com")).thenReturn(false).thenReturn(true);
        when(passwordService.hash("Pass123!")).thenReturn("hashed");
        when(repo.save(any(AppUser.class))).thenAnswer(inv -> {
            AppUser u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        UserResponse first = authService.register(
                new RegisterRequest("user1@example.com", "Pass123!"), Role.USER);
        assertEquals(1L, first.id());
        assertEquals("user1@example.com", first.email());
        assertEquals("USER", first.role());
        assertEquals("AVAILABLE", first.status());

        assertThrows(IllegalArgumentException.class, () ->
                authService.register(new RegisterRequest("user1@example.com", "Pass123!"), Role.USER)
        );
    }

    @Test
    void login_success() {
        AppUser u = new AppUser();
        u.setId(5L);
        u.setEmail("login@test.com");
        u.setPasswordHash("hash");
        u.setRole(Role.USER);
        u.setStatus(UserStatus.AVAILABLE);

        when(repo.findByEmail("login@test.com")).thenReturn(Optional.of(u));
        when(passwordService.matches("Pass123!", "hash")).thenReturn(true);
        when(jwt.generateToken(anyString(), anyString())).thenReturn("dummy-token");

        AuthResponse res = authService.login(new LoginRequest("login@test.com", "Pass123!"));

        assertEquals("USER", res.role());
        assertEquals("dummy-token", res.token());
    }

    @Test
    void login_wrongPassword_throws() {
        AppUser u = new AppUser();
        u.setId(7L);
        u.setEmail("wrong@test.com");
        u.setPasswordHash("hash");
        u.setRole(Role.USER);
        u.setStatus(UserStatus.AVAILABLE);

        when(repo.findByEmail("wrong@test.com")).thenReturn(Optional.of(u));
        when(passwordService.matches("bad", "hash")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
                authService.login(new LoginRequest("wrong@test.com", "bad"))
        );
    }

    @Test
    void login_blockedUser_throws() {
        AppUser u = new AppUser();
        u.setId(9L);
        u.setEmail("blocked@test.com");
        u.setPasswordHash("hash");
        u.setRole(Role.USER);
        u.setStatus(UserStatus.BLOCKED);

        when(repo.findByEmail("blocked@test.com")).thenReturn(Optional.of(u));
        when(passwordService.matches("Pass123!", "hash")).thenReturn(true);

        assertThrows(IllegalStateException.class, () ->
                authService.login(new LoginRequest("blocked@test.com", "Pass123!"))
        );
    }
}
