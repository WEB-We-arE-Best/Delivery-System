package com.webest.order.infrastructure.messaging.consumer;

import com.webest.order.application.service.OrderService;
import com.webest.order.infrastructure.messaging.events.DeliveryCompletedEvent;
import com.webest.order.infrastructure.messaging.events.DeliveryRollbackEvent;
import com.webest.order.infrastructure.messaging.events.PaymentCompletedEvent;
import com.webest.order.infrastructure.serialization.EventSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeliveryEventConsumer {

    private final OrderService orderService;

    @KafkaListener(topics = "delivery-completed", groupId = "order-group")
    public void handleDeliveryCompletedEvent(String message) {
        DeliveryCompletedEvent deliveryCompletedEvent = EventSerializer.deserialize(message, DeliveryCompletedEvent.class);
        orderService.completeOrder(deliveryCompletedEvent.getOrderId());
    }

    @KafkaListener(topics = "delivery-rollback", groupId = "order-group")
    public void handleDeliveryRollbackEvent(String message) {
        DeliveryRollbackEvent deliveryRollbackEvent = EventSerializer.deserialize(message, DeliveryRollbackEvent.class);
        orderService.rollbackOrder(deliveryRollbackEvent.getOrderId());
    }
}
