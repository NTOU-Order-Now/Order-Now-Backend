package com.ordernow.backend.notification.model.dto;

import com.ordernow.backend.order.model.entity.OrderedStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private String orderId;
    private String storeId;
    private OrderedStatus status;
    private String timestamp;
}
