package com.aly.ecomapp.service;

import com.aly.ecomapp.dto.AuthResponse;
import com.aly.ecomapp.dto.LoginRequest;
import com.aly.ecomapp.dto.RegisterRequest;
import com.aly.ecomapp.dto.UserResponse;
import com.aly.ecomapp.entity.AppUser;
import com.aly.ecomapp.entity.Role;
import com.aly.ecomapp.entity.UserStatus;
import com.aly.ecomapp.repository.AppUserRepository;
import com.aly.ecomapp.security.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AppUserRepository repo;
    private final PasswordService passwords;
    private final JwtUtil jwt; // <-- added

    public AuthService(AppUserRepository repo, PasswordService passwords, JwtUtil jwt) {
        this.repo = repo;
        this.passwords = passwords;
        this.jwt = jwt; // <-- store reference
    }

    public UserResponse register(RegisterRequest in, Role role) {
        String email = in.email().toLowerCase();
        if (repo.existsByEmail(email)) throw new IllegalArgumentException("Email already exists");

        AppUser u = new AppUser();
        u.setEmail(email);
        u.setPasswordHash(passwords.hash(in.password()));
        u.setRole(role);
        u.setStatus(UserStatus.AVAILABLE);
        repo.save(u);

        return map(u);
    }

    public AuthResponse login(LoginRequest in) {
        var user = repo.findByEmail(in.email().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwords.matches(in.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new IllegalStateException("User is blocked");
        }

        // Now we can use jwt
        String token = jwt.generateToken(user.getEmail(), "ROLE_" + user.getRole().name());
        return new AuthResponse(token, user.getRole().name());
    }

    private UserResponse map(AppUser u) {
        return new UserResponse(
                u.getId(),
                u.getEmail(),
                u.getRole().name(),
                u.getStatus().name()
        );
    }
}
