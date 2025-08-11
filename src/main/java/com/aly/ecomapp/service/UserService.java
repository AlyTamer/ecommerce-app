package com.aly.ecomapp.service;

import com.aly.ecomapp.Entity.AppUser;
import com.aly.ecomapp.Entity.UserStatus;
import com.aly.ecomapp.dto.UserResponse;
import com.aly.ecomapp.repository.AppUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService {

    private final AppUserRepository repo;

    public UserService(AppUserRepository repo) {
        this.repo = repo;
    }

    /** List all users (open for now; will be secured later). */
    public List<UserResponse> listAll() {
        return repo.findAll().stream().map(this::map).toList();
    }

    /** Get one user by id. */
    public UserResponse get(Long id) {
        var u = repo.findById(id).orElseThrow(() -> new NoSuchElementException("User not found"));
        return map(u);
    }

    /** Update a user's status to NONE or BLOCKED. */
    public UserResponse updateStatus(Long id, String statusRaw) {
        UserStatus status;
        try {
            status = UserStatus.valueOf(statusRaw.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("status must be NONE or BLOCKED");
        }

        var u = repo.findById(id).orElseThrow(() -> new NoSuchElementException("User not found"));
        u.setStatus(status);
        repo.save(u);
        return map(u);
    }

    /** Delete a user by id. */
    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new NoSuchElementException("User not found");
        repo.deleteById(id);
    }

    /* ------- helpers ------- */
    private UserResponse map(AppUser u) {
        return new UserResponse(
                u.getId(),
                u.getEmail(),
                u.getRole().name(),
                u.getStatus().name()
        );
    }
}
