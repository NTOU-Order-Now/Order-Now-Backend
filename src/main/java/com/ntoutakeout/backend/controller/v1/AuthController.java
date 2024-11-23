package com.ntoutakeout.backend.controller.v1;

import com.ntoutakeout.backend.dto.auth.LoginRequest;
import com.ntoutakeout.backend.entity.user.Customer;
import com.ntoutakeout.backend.entity.user.User;
import com.ntoutakeout.backend.service.AuthService;
import com.ntoutakeout.backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @Autowired
    public AuthController(AuthService userService, UserService authService) {
        this.authService = userService;
        this.userService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> signUpUser(@RequestBody Customer user) {
        log.info("Fetch API: register Success");
        try {
            authService.createUser(user);
            return ResponseEntity.status(HttpStatus.OK).body("Success");
        } catch (Exception e) {
            log.error("Signup failed for user: {}", user.getEmail(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            String token = authService.verify(loginRequest);
            User user = userService.getUserByEmail(loginRequest.getEmail());
            log.info("Fetch API: login Success");
            return ResponseEntity.status(HttpStatus.OK)
                    .header("Authorization", "Bearer " + token)
                    .body(user.getId());
        } catch (Exception e) {
            log.error("Login failed for email: {}", loginRequest.getEmail(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed: " + e.getMessage());
        }
    }

}
