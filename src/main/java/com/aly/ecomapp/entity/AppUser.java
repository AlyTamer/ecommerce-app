package com.aly.ecomapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Getter
@Setter
public class AppUser {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.AVAILABLE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "password_reset_token")
    private String passwordResetToken;

    @Column(name = "password_reset_expires")
    private Instant passwordResetExpires;

    @PrePersist
    public void onCreate() { this.createdAt = Instant.now(); }

    @Column(name = "password_reset_otp")
    private String passwordResetOtp;

    @Column(name = "password_reset_otp_expires")
    private Instant passwordResetOtpExpires;

//    getters/setters
//    public Long getId() { return id; }
//    public void setId(Long id) { this.id = id; }
//    public String getEmail() { return email; }
//    public void setEmail(String email) { this.email = email; }
//    public String getPasswordHash() { return passwordHash; }
//    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
//    public Role getRole() { return role; }
//    public void setRole(Role role) { this.role = role; }
//    public UserStatus getStatus() { return status; }
//    public void setStatus(UserStatus status) { this.status = status; }
//    public Instant getCreatedAt() { return createdAt; }
}
