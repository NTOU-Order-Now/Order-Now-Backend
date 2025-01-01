package com.ordernow.backend.auth.controller.v1;

import com.ordernow.backend.common.dto.ApiResponse;
import com.ordernow.backend.firebase.service.FirebaseEmailService;
import com.ordernow.backend.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController("EmailControllerV1")
@RequestMapping("/api/v1/email")
@Slf4j
public class EmailController {

    private final UserService userService;
    private final FirebaseEmailService firebaseEmailService;

    public EmailController(UserService userService, FirebaseEmailService firebaseEmailService) {
        this.userService = userService;
        this.firebaseEmailService = firebaseEmailService;
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestParam String email)
            throws NoSuchElementException {

        userService.findByEmail(email);
        firebaseEmailService.sendPasswordResetEmail(email);
        ApiResponse<Void> apiResponse = ApiResponse.success(null);
        log.info("Send password reset email successfully");
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
