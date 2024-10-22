package com.webest.order.infrastructure.messaging.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRollbackEvent {

    private Long id;

    private Long orderId;

    private String reason;
}
