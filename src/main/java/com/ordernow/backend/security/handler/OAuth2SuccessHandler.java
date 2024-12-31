package com.ordernow.backend.security.handler;

import com.ordernow.backend.auth.model.dto.LoginResponse;
import com.ordernow.backend.common.dto.ApiResponse;
import com.ordernow.backend.security.jwt.JWTService;
import com.ordernow.backend.user.model.entity.LoginType;
import com.ordernow.backend.user.model.entity.User;
import com.ordernow.backend.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTService jwtService;
    private final UserService userService;

    @Autowired
    public OAuth2SuccessHandler(JWTService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, IllegalArgumentException {

        if (response.isCommitted()) {
            log.debug("Response has already been committed");
        }

        log.info("OAuth2 login successfully");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String avatarUrl = oAuth2User.getAttribute("picture");

        Map<String, Object> result = new HashMap<>();
        User user = userService.getUserByEmail(email);
        if(user == null) { // register
            String temporaryToken = jwtService.generateTemporaryToken(email);
            result.put("isNewUser", true);
            result.put("email", email);
            result.put("name", name);
            result.put("avatarUrl", avatarUrl);
            result.put("temporaryToken", temporaryToken);
            ApiResponse.handleSuccess(response, result);
        } else if(user.getLoginType() == LoginType.GOOGLE){ // login
            String token = jwtService.generateToken(email);
            LoginResponse loginResponse = LoginResponse.createResponse(user, token);
            ApiResponse.handleSuccess(response, loginResponse);
        } else {
            throw new IllegalArgumentException("Email already exists");
        }
    }
}
