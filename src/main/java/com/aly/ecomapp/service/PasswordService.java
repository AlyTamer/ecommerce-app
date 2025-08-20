package com.aly.ecomapp.service;

import com.aly.ecomapp.entity.AppUser;
import com.aly.ecomapp.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.MessagingException;

import java.time.Instant;
import java.util.UUID;

@Service
public class PasswordService {

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;


    public String hash(String raw) { return BCrypt.hashpw(raw, BCrypt.gensalt()); }
    public boolean matches(String raw, String hash) { return BCrypt.checkpw(raw, hash); }


    public void sendPasswordResetLink(String email) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate token
        String token = UUID.randomUUID().toString();
        Instant expiry = Instant.now().plusSeconds(24 * 60 * 60); // 24 hours

        user.setPasswordResetToken(token);
        user.setPasswordResetExpires(expiry);
        userRepository.save(user);

        // Send email
        String resetLink = "http://localhost:3000/reset-password?token=" + token; // Change to your frontend URL
        sendEmail(email, "Password Reset Request", "Click here to reset your password: " + resetLink);
    }

    // Reset password using token
    public void resetPassword(String token, String newPassword) {
        AppUser user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        if (user.getPasswordResetExpires().isBefore(Instant.now())) {
            throw new RuntimeException("Token has expired");
        }
// Update password
        user.setPasswordHash(hash(newPassword));
        user.setPasswordResetToken(null); // Clear token
        user.setPasswordResetExpires(null);
        userRepository.save(user);
    }

    // Helper: Send email
    private void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true); // true = multipart
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, false); // false = plain text (not HTML)
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }


    }
