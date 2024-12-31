package com.ordernow.backend.auth.service;

import com.ordernow.backend.auth.model.dto.LoginRequest;
import com.ordernow.backend.store.service.StoreService;
import com.ordernow.backend.user.model.entity.*;
import com.ordernow.backend.user.repository.UserRepository;
import com.ordernow.backend.security.jwt.JWTService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authManager;
    private final JWTService jwtService;
    private final static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    private final StoreService storeService;

    @Autowired
    public AuthService(UserRepository userRepository,
                       AuthenticationManager authManager,
                       JWTService jwtService, StoreService storeService) {
        this.userRepository = userRepository;
        this.authManager = authManager;
        this.jwtService = jwtService;
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

    public void createUser(User user)
            throws IllegalArgumentException {

        validateEmail(user.getEmail());
        validateName(user.getName());
        user.setId(null);
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

        User user = userRepository.findByEmail(loginRequest.getEmail());
        if(user != null && user.getLoginType() == LoginType.GOOGLE) {
            log.error("Please use Google login");
            throw new AuthenticationServiceException("Please use Google login");
        }

        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        if(!authentication.isAuthenticated()) {
            log.error("Authentication failed");
            throw new AuthenticationServiceException("Authentication failed");
        }
        return jwtService.generateToken(loginRequest.getEmail());
    }
}
