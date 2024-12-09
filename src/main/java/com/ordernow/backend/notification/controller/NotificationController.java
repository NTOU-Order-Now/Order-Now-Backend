package com.ordernow.backend.notification.controller;

import com.ordernow.backend.notification.model.dto.Notification;
import com.ordernow.backend.order.model.entity.Order;
import com.ordernow.backend.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class NotificationController {

    private final OrderService orderService;

    @Autowired
    public NotificationController(OrderService orderService) {
        this.orderService = orderService;
    }

    @MessageMapping("/order-tracker/{orderId}")
    @SendTo("/topic/order/{orderId}")
    public Notification trackOrder(@DestinationVariable String orderId) {

        Order order = orderService.getOrderAndValid(orderId);
        Notification notification = new Notification(
                orderId,
                order.getStatus(),
                java.time.Instant.now().toString()
        );
        log.info("Sending notification for orderId: {}", orderId);
        log.info(notification.toString());
        return notification;
    }
}
