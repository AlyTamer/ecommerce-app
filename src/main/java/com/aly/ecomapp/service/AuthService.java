package com.aly.ecomapp.service;

import com.aly.ecomapp.dto.AuthResponse;
import com.aly.ecomapp.dto.LoginRequest;
import com.aly.ecomapp.dto.RegisterRequest;
import com.aly.ecomapp.dto.UserResponse;
import com.aly.ecomapp.entity.AppUser;
import com.aly.ecomapp.entity.Role;
import com.aly.ecomapp.entity.UserStatus;
import com.aly.ecomapp.exception.AuthException;
import com.aly.ecomapp.exception.AuthExceptionMessages;
import com.aly.ecomapp.exception.UserException;
import com.aly.ecomapp.exception.UserExceptionMessages;
import com.aly.ecomapp.repository.AppUserRepository;
import com.aly.ecomapp.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AppUserRepository repo;
    private final PasswordService passwords;
    private final JwtUtil jwt;

    @Autowired
    public AuthService(AppUserRepository repo, PasswordService passwords, JwtUtil jwt) {
        this.repo = repo;
        this.passwords = passwords;
        this.jwt = jwt;
    }

    public UserResponse register(RegisterRequest in, Role role) {
        String email = in.email().toLowerCase();
        if (repo.existsByEmail(email)) throw new AuthException(AuthExceptionMessages.EMAIL_ALREADY_EXISTS);

        AppUser u = new AppUser();
        u.setEmail(email);
        u.setPasswordHash(passwords.hash(in.password()));
        u.setRole(role);
        u.setStatus(UserStatus.AVAILABLE);
        repo.save(u);
        String token = jwt.generateToken(u.getEmail(), "ROLE_" + u.getRole().name());
        System.out.println(token);

        return map(u);
    }

    public AuthResponse login(LoginRequest in) {
        var user = repo.findByEmail(in.email().toLowerCase())
                .orElseThrow(() -> new AuthException(AuthExceptionMessages.INVALID_CREDENTIALS));

        if (!passwords.matches(in.password(), user.getPasswordHash())) {
            throw new AuthException(AuthExceptionMessages.INVALID_CREDENTIALS);
        }

        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new UserException(UserExceptionMessages.USER_IS_BLOCKED);
        }

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
