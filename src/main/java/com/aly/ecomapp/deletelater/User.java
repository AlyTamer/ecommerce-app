package com.aly.ecomapp.deletelater;

import jakarta.persistence.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import javax.management.relation.Role;

@Entity
@Table(name= "users")
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true,
    nullable = false)
    private String username;
    private String password;
    private Role role;
    @Column(unique = true)
    private String email;
}
