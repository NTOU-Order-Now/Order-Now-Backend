package com.ordernow.backend.user.controller.v1;

import com.ordernow.backend.auth.model.entity.CustomUserDetail;
import com.ordernow.backend.common.dto.ApiResponse;
import com.ordernow.backend.common.exception.RequestValidationException;
import com.ordernow.backend.common.validation.RequestValidator;
import com.ordernow.backend.user.model.dto.UserProfileRequest;
import com.ordernow.backend.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController("UserControllerV1")
@RequestMapping("/api/v1/user")
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PatchMapping()
    public ResponseEntity<ApiResponse<Void>> updateProfile(
            @RequestBody UserProfileRequest userProfileRequest,
            @AuthenticationPrincipal CustomUserDetail customUserDetail)
            throws NoSuchElementException, RequestValidationException {

        RequestValidator.validateRequest(userProfileRequest);
        userService.updateProfile(customUserDetail.getId(), userProfileRequest);
        log.info("User profile updated successfully");
        ApiResponse<Void> apiResponse = ApiResponse.success(null);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
