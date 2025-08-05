package com.aly.ecomapp.testing;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name= "users")
@Getter
@Setter
public class TestUser {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true,
    nullable = false)
    private String username;
    private String password;
    @Enumerated(EnumType.STRING)
    private TestRoles role;
    @Column(unique = true)
    private String email;
}
