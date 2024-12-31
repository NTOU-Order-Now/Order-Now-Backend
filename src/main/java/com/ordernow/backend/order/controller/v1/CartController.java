package com.ordernow.backend.order.controller.v1;

import com.ordernow.backend.auth.model.entity.CustomUserDetail;
import com.ordernow.backend.common.dto.ApiResponse;
import com.ordernow.backend.common.exception.RequestValidationException;
import com.ordernow.backend.common.validation.RequestValidator;
import com.ordernow.backend.order.model.dto.NoteRequest;
import com.ordernow.backend.order.model.dto.OrderedDishPatchRequest;
import com.ordernow.backend.order.model.dto.OrderedDishRequest;
import com.ordernow.backend.order.model.entity.Order;
import com.ordernow.backend.order.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController("CartControllerV1")
@RequestMapping("/api/v1/cart")
@PreAuthorize("hasRole('CUSTOMER')")
@Slf4j
public class CartController {

    public final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<Order>> getCart(
            @AuthenticationPrincipal CustomUserDetail customUserDetail)
            throws NoSuchElementException {

        Order cartOrder = cartService.getOrCreateCart(customUserDetail.getId());
        ApiResponse<Order> apiResponse = ApiResponse.success(cartOrder);
        log.info("Customer get cart successfully");
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @DeleteMapping()
    public ResponseEntity<ApiResponse<Void>> deleteCart(
            @AuthenticationPrincipal CustomUserDetail customUserDetail)
            throws NoSuchElementException {

        cartService.deleteCart(customUserDetail.getId());
        ApiResponse<Void> apiResponse = ApiResponse.success(null);
        log.info("Customer delete cart successfully");
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PostMapping("/dishes")
    public ResponseEntity<ApiResponse<String>> addNewDish(
            @RequestBody OrderedDishRequest orderedDishRequest,
            @AuthenticationPrincipal CustomUserDetail customUserDetail)
            throws NoSuchElementException, IllegalArgumentException, RequestValidationException {

        RequestValidator.validateRequest(orderedDishRequest);
        String orderedDishId = cartService.addNewDish(customUserDetail.getId(), orderedDishRequest);
        ApiResponse<String> apiResponse = ApiResponse.success(orderedDishId);
        log.info("Adding new dishes successfully");
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PatchMapping("/dishes/{orderedDishId}")
    public ResponseEntity<ApiResponse<String>> updateDish(
            @PathVariable("orderedDishId") String orderedDishId,
            @RequestBody OrderedDishPatchRequest request,
            @AuthenticationPrincipal CustomUserDetail customUserDetail)
            throws NoSuchElementException, IllegalArgumentException {

        Order cartOrder = cartService.updateDish(customUserDetail.getId(), orderedDishId, request);
        ApiResponse<String> apiResponse = ApiResponse.success(orderedDishId);
        log.info("Customer update dish successfully");
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PatchMapping("/send")
    public ResponseEntity<ApiResponse<Order>> sendOrder(
            @RequestBody NoteRequest noteRequest,
            @AuthenticationPrincipal CustomUserDetail customUserDetail)
            throws NoSuchElementException {

        RequestValidator.validateRequest(noteRequest);
        Order cartOrder = cartService.sendOrder(customUserDetail.getId(), noteRequest.getNote());
        ApiResponse<Order> apiResponse = ApiResponse.success(cartOrder);
        log.info("Customer send order successfully");
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
