package com.ordernow.backend.security.filter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.ordernow.backend.common.dto.ApiResponse;
import com.ordernow.backend.security.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class SecurityFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public SecurityFilter(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getMethod().equals("OPTIONS")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authToken = request.getHeader("Authorization");
        if (authToken == null || !authToken.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authToken.substring(7);
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);

            String username = decodedToken.getEmail();
            if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);
        } catch (FirebaseAuthException e) {
            log.error("Firebase authentication filed: {}", e.getMessage());
            ApiResponse.handleError(response, 401, "Unauthorized");
        } catch (UsernameNotFoundException e) {
            log.error("User not found: {}", e.getMessage());
            ApiResponse.handleError(response, 401, "User not found");
        } catch (AuthenticationException e) {
            log.error("Authentication failed: {}", e.getMessage());
            ApiResponse.handleError(response, 401, "Authentication failed");
        } catch (Exception e) {
            log.error("Unhandled exception: {}", e.getMessage());
            ApiResponse.handleError(response, 500, "Internal Server Error");
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // 排除 WebSocket 相關的路徑
        return path.startsWith("*/websocket/**");
    }
}
