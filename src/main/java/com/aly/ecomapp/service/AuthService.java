package com.aly.ecomapp.service;

import com.aly.ecomapp.dto.*;
import com.aly.ecomapp.entity.AppUser;
import com.aly.ecomapp.entity.Role;
import com.aly.ecomapp.entity.UserStatus;
import com.aly.ecomapp.exception.AuthException;
import com.aly.ecomapp.exception.AuthExceptionMessages;
import com.aly.ecomapp.exception.UserException;
import com.aly.ecomapp.exception.UserExceptionMessages;
import com.aly.ecomapp.repository.AppUserRepository;
import com.aly.ecomapp.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AppUserRepository repo;
    private final PasswordService passwords;
    private final JwtUtil jwt;
    private final JavaMailSender mailSender;

    @Autowired
    public AuthService(AppUserRepository repo, PasswordService passwords, JwtUtil jwt, JavaMailSender mailSender) {
        this.mailSender = mailSender;
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
    private void generateOTP(AppUser u) {
        String otp="";
        for(int i=0;i<6;i++){
            int x =(int) (Math.random()*10);
            otp+=x;
        }
        u.setOtp(otp);
        System.out.println("Otp is :"+otp);
        repo.save(u);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("mailapiecomapp@gmail.com");
        message.setTo(u.getEmail());
        message.setSubject("Your OTP to Reset Password");
        message.setText("Below is your otp to reset your password:\n"+otp);
        mailSender.send(message);



    }

    public String  resetPassword(@Valid ResetRequest req) {
        AppUser user = repo.findByEmail(req.getEmail().toLowerCase())
                .orElseThrow(() -> new AuthException(AuthExceptionMessages.INVALID_CREDENTIALS));

        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new UserException(UserExceptionMessages.USER_IS_BLOCKED);
        }
        if (user.getOtp() == null) {
            generateOTP(user);

            return "OTP sent to " + user.getEmail();
        }
        else{
            return "OTP Already Sent to " + user.getEmail();
        }

    }

    public Object changePassword(@Valid ForgotPasswordRequest req) {
    AppUser user = repo.findByEmail(req.getEmail().toLowerCase())
            .orElseThrow(() -> new AuthException(AuthExceptionMessages.INVALID_CREDENTIALS));
    if (user.getStatus() == UserStatus.BLOCKED) {
        throw new UserException(UserExceptionMessages.USER_IS_BLOCKED);
    }
        if (user.getPasswordHash()==passwords.hash(req.getOldPassword())) {
            if (user.getPasswordHash()==passwords.hash(req.getNewPassword())) {
                throw new AuthException(AuthExceptionMessages.PASSWORD_ALREADY_EXISTS);
            }
            user.setPasswordHash(passwords.hash(req.getNewPassword()));
            repo.save(user);
            String token = jwt.generateToken(user.getEmail(), "ROLE_" + user.getRole().name());
            return new AuthResponse(token, user.getRole().name());
        }

throw new AuthException(AuthExceptionMessages.INVALID_CREDENTIALS);
    }

    public AuthResponse confirmReset(@Valid ConfirmResetRequest req) {
        AppUser user = repo.findByEmail(req.getEmail().toLowerCase())
                .orElseThrow(() -> new AuthException(AuthExceptionMessages.INVALID_CREDENTIALS));
        if( user.getOtp() == null || !user.getOtp().equals(req.getOtp())) {
            throw new AuthException(AuthExceptionMessages.INVALID_OTP);
        }
        user.setPasswordHash(passwords.hash(req.getNewPassword()));
        user.setOtp(null);
        repo.save(user);
        String token = jwt.generateToken(user.getEmail(), "ROLE_" + user.getRole().name());
        return new AuthResponse(token, user.getRole().name());
    }
}
