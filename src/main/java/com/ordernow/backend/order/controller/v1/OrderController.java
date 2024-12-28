package com.ordernow.backend.order.controller.v1;

import com.ordernow.backend.auth.model.entity.CustomUserDetail;
import com.ordernow.backend.common.dto.ApiResponse;
import com.ordernow.backend.common.dto.PageResponse;
import com.ordernow.backend.order.model.entity.Order;
import com.ordernow.backend.order.model.entity.OrderedStatus;
import com.ordernow.backend.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@RestController("CustomerControllerV1")
@RequestMapping("api/v1/order")
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private static final Set<String> ALLOWED_ORDER_STATUS = Set.of("PENDING", "PROCESSING", "COMPLETED", "PICKED_UP", "CANCELED", "");

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<Void>> updateOrderStatus(
            @PathVariable("orderId") String orderId,
            @RequestParam(value = "status") OrderedStatus status,
            @AuthenticationPrincipal CustomUserDetail customUserDetail)
            throws NoSuchElementException, IllegalArgumentException, IllegalStateException {

        if(!ALLOWED_ORDER_STATUS.contains(status.toString())) {
            throw new IllegalArgumentException("Invalid order status");
        }

        orderService.updateStatus(customUserDetail, orderId, status);
        ApiResponse<Void> apiResponse = ApiResponse.success(null);
        log.info("Update order status to {} successfully", status);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PatchMapping("/{orderId}/pickup-time")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<Void>> updatePickupTime(
            @PathVariable("orderId") String orderId,
            @RequestParam(value = "pickupTime") int pickupTime)
            throws NoSuchElementException {

        orderService.updatePickupTime(orderId, pickupTime);
        ApiResponse<Void> apiResponse = ApiResponse.success(null);
        log.info("Update order pickup time to {} successfully", pickupTime);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<Order>>> searchOrder(
            @RequestParam(value="page", defaultValue = "0") int page,
            @RequestParam(value="size", defaultValue = "10") int size,
            @RequestParam(value="status", required = false) OrderedStatus status,
            @AuthenticationPrincipal CustomUserDetail customUserDetail)
            throws NoSuchElementException, IllegalArgumentException {

        if(status != null && !ALLOWED_ORDER_STATUS.contains(status.toString())) {
            throw new IllegalArgumentException("Invalid order status");
        }

        PageResponse<Order> orders = orderService.getOrderListByStatus(
                customUserDetail, status, page, size);
        ApiResponse<PageResponse<Order>> apiResponse = ApiResponse.success(orders);
        log.info("User search order successfully");
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
