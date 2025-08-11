package com.aly.ecomapp;

import com.aly.ecomapp.dto.UserResponse;
import com.aly.ecomapp.entity.AppUser;
import com.aly.ecomapp.entity.Role;
import com.aly.ecomapp.entity.UserStatus;
import com.aly.ecomapp.repository.AppUserRepository;
import com.aly.ecomapp.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    @Mock AppUserRepository repo;
    @InjectMocks UserService userService;

    private AppUser makeUser(Long id, String email, Role role, UserStatus status) {
        AppUser u = new AppUser();
        u.setId(id);
        u.setEmail(email);
        u.setPasswordHash("hash");
        u.setRole(role);
        u.setStatus(status);
        return u;
    }

    @Test
    void listAll_mapsToDtos() {
        when(repo.findAll()).thenReturn(List.of(
                makeUser(1L, "a@test.com", Role.USER, UserStatus.AVAILABLE),
                makeUser(2L, "b@test.com", Role.ADMIN, UserStatus.BLOCKED)
        ));
        var out = userService.listAll();
        assertEquals(2, out.size());
        assertEquals("a@test.com", out.get(0).email());
        assertEquals("ADMIN", out.get(1).role());
        assertEquals("BLOCKED", out.get(1).status());
    }

    @Test
    void get_ok() {
        when(repo.findById(10L)).thenReturn(Optional.of(
                makeUser(10L, "x@test.com", Role.USER, UserStatus.AVAILABLE)
        ));
        UserResponse r = userService.get(10L);
        assertEquals(10L, r.id());
        assertEquals("x@test.com", r.email());
    }

    @Test
    void get_notFound_throws() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> userService.get(99L));
    }

    @Test
    void updateStatus_validValues() {
        AppUser u = makeUser(3L, "z@test.com", Role.USER, UserStatus.AVAILABLE);
        when(repo.findById(3L)).thenReturn(Optional.of(u));
        when(repo.save(any(AppUser.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResponse r1 = userService.updateStatus(3L, "BLOCKED");
        assertEquals("BLOCKED", r1.status());

        UserResponse r2 = userService.updateStatus(3L, "AVAILABLE");
        assertEquals("AVAILABLE", r2.status());
    }

    @Test
    void updateStatus_invalidValue_throws() {
        // No stubbing here — service will throw before hitting the repo
        assertThrows(IllegalArgumentException.class, () ->
                userService.updateStatus(3L, "SUSPENDED")
        );

        // Optional: assert that the repo was never touched
        org.mockito.Mockito.verifyNoInteractions(repo);
    }

    @Test
    void delete_ok() {
        when(repo.existsById(4L)).thenReturn(true);
        userService.delete(4L);
        verify(repo).deleteById(4L);
    }

    @Test
    void delete_notFound_throws() {
        when(repo.existsById(5L)).thenReturn(false);
        assertThrows(NoSuchElementException.class, () -> userService.delete(5L));
        verify(repo, never()).deleteById(anyLong());
    }
}
