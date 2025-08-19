package com.aly.ecomapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter@Setter
public class ForgotPasswordRequest {
    String email;
    String oldPassword;
    String newPassword;
}
