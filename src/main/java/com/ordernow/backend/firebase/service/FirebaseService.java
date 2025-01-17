package com.ordernow.backend.firebase.service;

import com.google.firebase.auth.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FirebaseService {

    private final FirebaseAuth firebaseAuth;

    @Autowired
    public FirebaseService(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    public FirebaseToken verifyToken(String token)
            throws FirebaseAuthException {

        return firebaseAuth.verifyIdToken(token);
    }

    public UserRecord createUser(String email, String password)
            throws FirebaseAuthException {

        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setPassword(password)
                .setEmailVerified(false);

        UserRecord userRecord = firebaseAuth.createUser(request);
        sendEmailVerification(email);
        return userRecord;
    }

    public void sendEmailVerification(String email)
            throws FirebaseAuthException {
        String link = firebaseAuth.generateEmailVerificationLink(email);
        log.info("Sending email verification email to {}", email);
        System.out.println(link);
    }
}
