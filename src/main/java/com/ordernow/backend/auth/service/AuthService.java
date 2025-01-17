package com.ordernow.backend.auth.service;

import com.ordernow.backend.auth.model.dto.LoginRequest;
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

    public void validateEmail(String email) {
        if(userRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException("Email already exists");
        }
    }

    public void validateName(String name) {
        if(name.length() > 20) {
            throw new IllegalArgumentException("Name is too long");
        }
    }

    public void register(User user)
            throws IllegalArgumentException {

        validateEmail(user.getEmail());
        validateName(user.getName());
        user.setPassword(encoder.encode(user.getPassword()));

        if(user.getLoginType() == LoginType.GOOGLE) {
            user.setPassword("");
        }

        if(user.getRole() == Role.MERCHANT && user.getPhoneNumber().isEmpty()){
            throw new IllegalArgumentException("Merchant phone number can not be empty");
        }

        User savedUser = switch(user.getRole()) {
            case CUSTOMER -> new Customer(user);
            case MERCHANT -> new Merchant(user, storeService.createAndSaveStore(user.getPhoneNumber()));
            default -> throw new IllegalArgumentException("Invalid role");
        };
        userRepository.save(savedUser);
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
