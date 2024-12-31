package com.ordernow.backend.auth.controller.v2;

import com.ordernow.backend.auth.model.dto.LoginResponse;
import com.ordernow.backend.common.dto.ApiResponse;
import com.ordernow.backend.auth.model.dto.LoginRequest;
import com.ordernow.backend.common.exception.RequestValidationException;
import com.ordernow.backend.common.validation.RequestValidator;
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

    @Autowired
    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> signUpUser(
            @RequestBody User user)
            throws IllegalArgumentException, RequestValidationException {

        RequestValidator.validateRequest(user);
        authService.createUser(user);
        ApiResponse<Void> apiResponse = ApiResponse.success(null);
        log.info("Sign up user successfully");
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> loginUser(
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
}
