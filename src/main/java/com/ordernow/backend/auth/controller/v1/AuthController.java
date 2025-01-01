package com.ordernow.backend.auth.controller.v1;

import com.ordernow.backend.auth.model.dto.LoginRequest;
import com.ordernow.backend.user.model.entity.Customer;
import com.ordernow.backend.user.model.entity.User;
import com.ordernow.backend.auth.service.AuthService;
import com.ordernow.backend.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("AuthControllerV1")
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @Autowired
    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
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

//    @PostMapping("/login")
//    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
//        try {
//            String token = authService.verify(loginRequest);
//            User user = userService.findByEmail(loginRequest.getEmail());
//            log.info("Fetch API: login Success");
//            return ResponseEntity.status(HttpStatus.OK)
//                    .header("Authorization", "Bearer " + token)
//                    .body(user.getId());
//        } catch (Exception e) {
//            log.error("Login failed for email: {}", loginRequest.getEmail(), e);
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed: " + e.getMessage());
//        }
//    }
}
