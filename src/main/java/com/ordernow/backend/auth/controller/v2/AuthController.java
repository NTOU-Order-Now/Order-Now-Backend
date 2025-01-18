package com.ordernow.backend.auth.controller.v2;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.ordernow.backend.auth.model.dto.LoginResponse;
import com.ordernow.backend.auth.model.dto.RegisterRequest;
import com.ordernow.backend.common.dto.ApiResponse;
import com.ordernow.backend.auth.model.dto.LoginRequest;
import com.ordernow.backend.common.exception.RequestValidationException;
import com.ordernow.backend.common.validation.RequestValidator;
import com.ordernow.backend.firebase.service.FirebaseService;
import com.ordernow.backend.user.model.entity.User;
import com.ordernow.backend.auth.service.AuthService;
import com.ordernow.backend.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("AuthControllerV2")
@RequestMapping("/api/v2/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final FirebaseService firebaseService;

    @Autowired
    public AuthController(AuthService authService, UserService userService, FirebaseService firebaseService) {
        this.authService = authService;
        this.userService = userService;
        this.firebaseService = firebaseService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(
            @RequestBody RegisterRequest request)
            throws IllegalArgumentException, RequestValidationException {

        RequestValidator.validateRequest(request);
        authService.register(request);
        ApiResponse<Void> apiResponse = ApiResponse.success(null);
        log.info("Sign up user successfully");
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @RequestBody LoginRequest loginRequest)
            throws AuthenticationServiceException, RequestValidationException {

        RequestValidator.validateRequest(loginRequest);
        String token = authService.verify(loginRequest);
        User user = userService.getUserByEmail(loginRequest.getEmail());

        LoginResponse response = LoginResponse.createResponse(user, token);
        ApiResponse<LoginResponse> apiResponse = ApiResponse.success(response);
        log.info("Login user successfully");
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PostMapping("/firebase/register")
    public ResponseEntity<ApiResponse<Void>> firebaseRegister(
            @RequestBody RegisterRequest request)
            throws IllegalArgumentException, RequestValidationException, FirebaseAuthException {

        RequestValidator.validateRequest(request);
        UserRecord userRecord = firebaseService.createUser(request.getEmail(), request.getPassword(), request.getName());
        System.out.println(userRecord);
        ApiResponse<Void> apiResponse = ApiResponse.success(null);
        log.info("Register user successfully");
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
