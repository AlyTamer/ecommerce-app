package com.aly.ecomapp.service;

import com.aly.ecomapp.dto.UserResponse;
import com.aly.ecomapp.entity.AppUser;
import com.aly.ecomapp.entity.UserStatus;
import com.aly.ecomapp.exception.UserException;
import com.aly.ecomapp.exception.UserExceptionMessages;
import com.aly.ecomapp.repository.AppUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final AppUserRepository repo;

    public UserService(AppUserRepository repo) {
        this.repo = repo;
    }

    public List<UserResponse> listAll() {
        return repo.findAll().stream().map(this::map).toList();
    }

    public UserResponse get(Long id) {
        var u = repo.findById(id).orElseThrow(() -> new UserException(UserExceptionMessages.USER_NOT_FOUND));
        return map(u);
    }

    public UserResponse updateStatus(Long id, String statusRaw) {
        UserStatus status;
        try {
            status = UserStatus.valueOf(statusRaw.toUpperCase());
        } catch (Exception e) {
            throw new UserException(UserExceptionMessages.USER_NOT_BLOCKED);
        }

        var u = repo.findById(id).orElseThrow(() -> new UserException(UserExceptionMessages.USER_NOT_FOUND));
        u.setStatus(status);
        repo.save(u);
        return map(u);
    }


    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new UserException(UserExceptionMessages.USER_NOT_FOUND);
        repo.deleteById(id);
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
