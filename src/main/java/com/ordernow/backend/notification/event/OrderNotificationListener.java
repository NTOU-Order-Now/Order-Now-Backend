package com.ordernow.backend.notification.event;

import com.ordernow.backend.notification.model.dto.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderNotificationListener {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public OrderNotificationListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleOrderStatus(Notification notification) {
        log.info("Sending notification for orderId: {}", notification.getOrderId());
        log.info(notification.toString());
        //send to Customer
        messagingTemplate.convertAndSend("/topic/order/"+notification.getOrderId(), notification);
        //send to Merchant
        messagingTemplate.convertAndSend("/topic/order/send/"+notification.getStoreId(), notification);
    }
}
