package com.ordernow.backend.common.exception;

import com.ordernow.backend.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException exception) {
        ApiResponse<Void> apiResponse = ApiResponse.error(400, exception.getMessage());
        return ResponseEntity.status(400).body(apiResponse);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoSuchElementException(NoSuchElementException exception) {
        ApiResponse<Void> apiResponse = ApiResponse.error(404, exception.getMessage());
        return ResponseEntity.status(404).body(apiResponse);
    }

    @ExceptionHandler(AuthenticationServiceException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationServiceException(AuthenticationServiceException exception) {
        ApiResponse<Void> apiResponse = ApiResponse.error(401, exception.getMessage());
        return ResponseEntity.status(401).body(apiResponse);
    }

    @ExceptionHandler(RequestValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleRequestValidationException(RequestValidationException exception) {
        ApiResponse<Void> apiResponse = ApiResponse.error(400, exception.getMessage());
        return ResponseEntity.status(400).body(apiResponse);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalStateException(IllegalStateException exception) {
        ApiResponse<Void> apiResponse = ApiResponse.error(400, exception.getMessage());
        return ResponseEntity.status(400).body(apiResponse);
    }
}
