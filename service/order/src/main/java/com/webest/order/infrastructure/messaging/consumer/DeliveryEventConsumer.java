package com.webest.order.infrastructure.messaging.consumer;

import com.webest.order.application.service.OrderService;
import com.webest.order.domain.exception.ErrorCode;
import com.webest.order.domain.exception.OrderException;
import com.webest.order.domain.model.Order;
import com.webest.order.domain.repository.order.OrderRepository;
import com.webest.order.infrastructure.messaging.events.DeliveryCompletedEvent;
import com.webest.order.infrastructure.messaging.events.DeliveryCreatedEvent;
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
    private final OrderRepository orderRepository;


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


    @KafkaListener(topics = "delivery-created", groupId = "order-group")
    public void handleDeliveryCreatedEvent(String message) {
        DeliveryCreatedEvent deliveryCreatedEvent = EventSerializer.deserialize(message, DeliveryCreatedEvent.class);
        Order order = orderRepository.findById(deliveryCreatedEvent.getOrderId()).orElseThrow(() -> new OrderException(ErrorCode.ORDER_NOT_FOUND));
        order.setDeliveryId(deliveryCreatedEvent.getId());
        orderRepository.save(order);
    }

}
