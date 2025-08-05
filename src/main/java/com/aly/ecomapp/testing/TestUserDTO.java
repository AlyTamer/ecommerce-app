package com.aly.ecomapp.testing;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TestUserDTO {
    private String username;
    private String email;
    private String role;
    private String password;
}
