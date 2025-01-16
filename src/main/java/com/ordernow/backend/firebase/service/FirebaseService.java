package com.ordernow.backend.firebase.service;

import com.google.firebase.auth.FirebaseAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FirebaseService {

    private final FirebaseAuth firebaseAuth;

    public FirebaseService() {
        this.firebaseAuth = FirebaseAuth.getInstance();
    }
}
