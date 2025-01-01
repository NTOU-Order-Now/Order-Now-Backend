package com.ordernow.backend.firebase.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FirebaseEmailService {

    public void sendPasswordResetEmail(String email) {
        try {
            String link = FirebaseAuth.getInstance().generatePasswordResetLink(email);
            log.info("Password reset link generated successfully");
        } catch (FirebaseAuthException e) {
            log.error("Failed to send password reset email", e);
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    public void sendEmailVerification(String email) {
        try {
            String link = FirebaseAuth.getInstance().generateEmailVerificationLink(email);
            log.info("Email verification link generated successfully");
        } catch (FirebaseAuthException e) {
            log.error("Failed to send email verification link", e);
            throw new RuntimeException("Failed to send email verification link", e);
        }
    }
}
