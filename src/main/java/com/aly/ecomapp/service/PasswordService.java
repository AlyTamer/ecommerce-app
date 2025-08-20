package com.aly.ecomapp.service;

import com.aly.ecomapp.entity.AppUser;
import com.aly.ecomapp.exception.AuthException;
import com.aly.ecomapp.exception.AuthExceptionMessages;
import com.aly.ecomapp.repository.AppUserRepository;
import com.aly.ecomapp.security.JwtConfig;
import com.aly.ecomapp.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.MessagingException;

import java.time.Instant;
import java.util.Random;
import java.util.UUID;

@Service
public class PasswordService {

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
private JwtUtil jwtUtil;

    public String hash(String raw) { return BCrypt.hashpw(raw, BCrypt.gensalt()); }
    public boolean matches(String raw, String hash) { return BCrypt.checkpw(raw, hash); }


    public void sendPasswordResetLink(String email) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException(AuthExceptionMessages.USER_NOT_FOUND));


        String token = UUID.randomUUID().toString();
        Instant expiry = Instant.now().plusSeconds(24 * 60 * 60);

        user.setPasswordResetToken(token);
        user.setPasswordResetExpires(expiry);
        userRepository.save(user);


        String resetLink = "http://localhost:3000/reset-password?token=" + token;
        sendEmail(email, "Password Reset Request", "Click here to reset your password: " + resetLink);
    }


    public void resetPassword(String token, String newPassword) {
        AppUser user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new AuthException(AuthExceptionMessages.INVALID_TOKEN));

        if (user.getPasswordResetExpires().isBefore(Instant.now())) {
            throw new AuthException(AuthExceptionMessages.EXPIRED_TOKEN);
        }

        user.setPasswordHash(hash(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetExpires(null);
        userRepository.save(user);
    }


    private void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true); // true = multipart
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, false); // false = plain text (not HTML)
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new AuthException(AuthExceptionMessages.FAILED_TO_SENT_EMAIL);
        }
    }

    public void sendPasswordResetOtp(String email) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException(AuthExceptionMessages.INVALID_TOKEN));

        String otp = String.format("%06d", new Random().nextInt(1000000));

        Instant expiry = Instant.now().plusSeconds(10 * 60);

        user.setPasswordResetOtp(otp);
        user.setPasswordResetOtpExpires(expiry);
        userRepository.save(user);


        String subject = "Your Password Reset OTP";
        String body = "Your OTP to reset your password is: " + otp + "\nIt expires in 10 minutes.";

        sendEmail(email, subject, body);
    }

    public void verifyOtpAndResetPassword(String email, String otp, String newPassword) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException(AuthExceptionMessages.USER_NOT_FOUND));

        if (user.getPasswordResetOtp() == null || !user.getPasswordResetOtp().equals(otp)) {
            throw new AuthException(AuthExceptionMessages.INVALID_OTP);
        }

        if (user.getPasswordResetOtpExpires().isBefore(Instant.now())) {
            throw new AuthException(AuthExceptionMessages.OTP_EXPIRED);
        }


        user.setPasswordHash(hash(newPassword));

        user.setPasswordResetOtp(null);
        user.setPasswordResetOtpExpires(null);

        userRepository.save(user);
    }




    }
