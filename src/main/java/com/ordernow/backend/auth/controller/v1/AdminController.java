package com.ordernow.backend.auth.controller.v1;

import com.ordernow.backend.order.model.entity.OrderedStatus;
import com.ordernow.backend.order.repository.OrderRepository;
import com.ordernow.backend.user.service.UserService;
import com.ordernow.backend.common.dto.ApiResponse;
import com.ordernow.backend.user.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("AdminControllerV1")
@RequestMapping("/api/v1/admin")
@Slf4j
public class AdminController {
    private final UserService userService;
    private final OrderRepository orderRepository;

    @Autowired
    public AdminController(UserService userService, OrderRepository orderRepository) {
        this.userService = userService;
        this.orderRepository = orderRepository;
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {

        List<User> users = userService.getAllUsers();
        ApiResponse<List<User>> apiResponse = ApiResponse.success(users);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable String userId) {

        userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null));
    }

    @DeleteMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(
            @PathVariable String orderId) {

        orderRepository.deleteById(orderId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null));
    }

    @DeleteMapping("/order")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(
            @RequestParam OrderedStatus status) {

        orderRepository.deleteByStatus(status);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null));
    }
}
