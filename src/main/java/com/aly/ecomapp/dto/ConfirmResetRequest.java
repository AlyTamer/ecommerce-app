package com.aly.ecomapp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ConfirmResetRequest {
    String otp;
    String email;
    String newPassword;
}
