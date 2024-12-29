package com.ordernow.backend.common.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;

import java.io.IOException;

@Data
public class ApiResponse<T> {
    private int status;
    private String message;
    private String timestamp;
    private T data;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public ApiResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.timestamp = java.time.Instant.now().toString();
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<T>(200, "Success", data);
    }

    public static <T> ApiResponse<T> error(int status, String message) {
        return new ApiResponse<T>(status, message, null);
    }

    public static void handleError(HttpServletResponse response, int status, String message)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(
                objectMapper.writeValueAsString(error(status, message))
        );
    }
}
