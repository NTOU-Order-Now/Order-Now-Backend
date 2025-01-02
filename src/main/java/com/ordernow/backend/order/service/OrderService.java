package com.ordernow.backend.order.service;

import com.ordernow.backend.auth.model.entity.CustomUserDetail;
import com.ordernow.backend.common.dto.PageResponse;
import com.ordernow.backend.menu.service.MenuService;
import com.ordernow.backend.notification.model.dto.Notification;
import com.ordernow.backend.order.model.entity.Order;
import com.ordernow.backend.order.model.entity.OrderedDish;
import com.ordernow.backend.order.model.entity.OrderedStatus;
import com.ordernow.backend.order.repository.OrderRepository;
import com.ordernow.backend.user.model.entity.Merchant;
import com.ordernow.backend.user.model.entity.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;
    private static final Set<String> CUSTOMER_ALLOWED_TO_UPDATED_STATUS = Set.of("CANCELED", "PICKED_UP");
    private static final Set<String> MERCHANT_ALLOWED_TO_UPDATED_STATUS = Set.of("CANCELED", "PROCESSING", "COMPLETED", "PICKED_UP");
    private final MenuService menuService;


    @Autowired
    public OrderService(OrderRepository orderRepository, ApplicationEventPublisher eventPublisher, MenuService menuService) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
        this.menuService = menuService;
    }

    public Order getOrderAndValid(String orderId)
            throws NoSuchElementException {

        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            throw new NoSuchElementException("Order not found with ID: " + orderId);
        }

        return order;
    }

    public void updateStatus(CustomUserDetail userDetail, String orderId, OrderedStatus status)
            throws NoSuchElementException, IllegalStateException {

        Order order = getOrderAndValid(orderId);
        Role role = userDetail.getRole();

        if(status == OrderedStatus.PENDING) {
            throw new IllegalStateException("Order status can not be PENDING");
        }
        if(status == OrderedStatus.CANCELED && order.getStatus() != OrderedStatus.PENDING) {
            throw new IllegalStateException("Order is not in PENDING status");
        }
        if(status == OrderedStatus.PROCESSING && order.getStatus() != OrderedStatus.PENDING) {
            throw new IllegalStateException("Order is not in PENDING status");
        }
        if(status == OrderedStatus.COMPLETED && order.getStatus() != OrderedStatus.PROCESSING) {
            throw new IllegalStateException("Order is not in PROCESSING status");
        }
        if(status == OrderedStatus.PICKED_UP && order.getStatus() != OrderedStatus.COMPLETED) {
            throw new IllegalStateException("Order is not in COMPLETED status");
        }

        if(role == Role.CUSTOMER && !CUSTOMER_ALLOWED_TO_UPDATED_STATUS.contains(status.toString())) {
            throw new IllegalStateException("Customer is not allowed to update status");
        }
        if(role == Role.MERCHANT && !MERCHANT_ALLOWED_TO_UPDATED_STATUS.contains(status.toString())) {
            throw new IllegalStateException("Merchant is not allowed to update status");
        }

        order.setStatus(status);
        if(status == OrderedStatus.PROCESSING) {
            order.setAcceptTime(LocalDateTime.now());
        }
        if(status == OrderedStatus.PICKED_UP) {
            updateSalesVolume(order.getOrderedDishes());
        }

        orderRepository.save(order);
        eventPublisher.publishEvent(
                new Notification(orderId,
                        order.getStatus(),
                        java.time.Instant.now().toString()
                )
        );
    }

    public PageResponse<Order> getOrderListByStatus(
            CustomUserDetail userDetail, OrderedStatus status,
            int page, int size)
            throws IllegalStateException {

        if(page < 0 || size <= 0) {
            throw new IllegalArgumentException("Invalid page number or page size");
        }

        Page<Order> orders = null;
        if(userDetail.getRole() == Role.CUSTOMER) {
            Sort sort = Sort.by(Sort.Direction.DESC, "orderTime");
            Pageable pageable = PageRequest.of(page, size, sort);
            if(status == null)
                orders = orderRepository.findAllByCustomerIdAndStatusNot(userDetail.getId(), OrderedStatus.IN_CART, pageable);
            else
                orders = orderRepository.findAllByCustomerIdAndStatus(userDetail.getId(), status, pageable);
        }
        else if(userDetail.getRole() == Role.MERCHANT) {
            Sort sort = Sort.by(Sort.Direction.ASC, "orderTime");
            Pageable pageable = PageRequest.of(page, size, sort);
            Merchant merchant = (Merchant) userDetail.getUser();
            if(status == null)
                orders = orderRepository.findAllByStoreIdAndStatusNot(merchant.getStoreId(), OrderedStatus.IN_CART, pageable);
            else
                orders = orderRepository.findAllByStoreIdAndStatus(merchant.getStoreId(), status, pageable);
        }
        else {
            return null;
        }

        return PageResponse.of(orders);
    }

    public void updatePickupTime(String orderId, int pickupTime)
            throws NoSuchElementException {

        Order order = getOrderAndValid(orderId);
        order.setIsReserved(true);
        order.setEstimatedPrepTime(pickupTime);
        orderRepository.save(order);
    }

    public void updateSalesVolume(List<OrderedDish> dishes) {
        for(OrderedDish dish : dishes) {
            menuService.updateSalesVolume(dish.getDishId(), dish.getQuantity());
        }
    }
}
