package com.ordernow.backend.auth.service;

import com.google.firebase.auth.UserRecord;
import com.ordernow.backend.auth.model.dto.LoginRequest;
import com.ordernow.backend.auth.model.dto.RegisterRequest;
import com.ordernow.backend.firebase.service.FirebaseService;
import com.ordernow.backend.store.service.StoreService;
import com.ordernow.backend.user.model.entity.*;
import com.ordernow.backend.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final FirebaseService firebaseService;
    private final StoreService storeService;
    private final static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Autowired
    public AuthService(UserRepository userRepository, FirebaseService firebaseService, StoreService storeService) {
        this.userRepository = userRepository;
        this.firebaseService = firebaseService;
        this.storeService = storeService;
    }

    public void register(RegisterRequest request)
            throws IllegalArgumentException {

        try {
            User user = request.toUser();

            UserRecord userRecord = firebaseService.createUser(
                    user.getEmail(),
                    user.getPassword(),
                    user.getName());

            user.setId(userRecord.getUid());
            user.setPassword(encoder.encode(user.getPassword()));

            User savedUser = switch(user.getRole()) {
                case CUSTOMER -> new Customer(user);
                case MERCHANT -> new Merchant(user, storeService.createAndSaveStore(user.getPhoneNumber()));
                default -> throw new IllegalArgumentException("Invalid role");
            };
            userRepository.save(savedUser);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(e);
        }
    }

    public String verify(LoginRequest loginRequest)
            throws AuthenticationServiceException {

        return null;
//        User user = userRepository.findByEmail(loginRequest.getEmail());
//        if(user != null && user.getLoginType() == LoginType.GOOGLE) {
//            log.error("Please use Google login");
//            throw new AuthenticationServiceException("Please use Google login");
//        }
//
//        Authentication authentication = authManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        loginRequest.getEmail(),
//                        loginRequest.getPassword()
//                )
//        );
//
//        if(!authentication.isAuthenticated()) {
//            log.error("Authentication failed");
//            throw new AuthenticationServiceException("Authentication failed");
//        }
//        return jwtService.generateToken(loginRequest.getEmail());
    }
}
