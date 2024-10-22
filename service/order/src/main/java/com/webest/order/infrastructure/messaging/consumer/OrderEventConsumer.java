package com.webest.order.infrastructure.messaging.consumer;

import com.webest.order.domain.events.*;
import com.webest.order.application.service.OrderQueryService;
import com.webest.order.infrastructure.serialization.EventSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final OrderQueryService orderQueryService;


    @KafkaListener(topics = "order-created", groupId = "order-group")
    public void handleOrderCreatedEvent(String message) {
        OrderCreatedEvent orderCreatedEvent = EventSerializer.deserialize(message, OrderCreatedEvent.class);
        orderQueryService.createOrderQuery(orderCreatedEvent);
    }

    @KafkaListener(topics = "order-payment-completed", groupId = "order-query-group")
    public void handleOrderPaymentCompletedEvent(String message) {
        OrderCompletedEvent orderCompletedEvent = EventSerializer.deserialize(message, OrderCompletedEvent.class);
        orderQueryService.completeOrder(orderCompletedEvent.getId());
    }

    @KafkaListener(topics = "order-canceled", groupId = "order-query-group")
    public void handleOrderCanceledEvent(String message) {
        OrderCanceledEvent orderCanceledEvent = EventSerializer.deserialize(message, OrderCanceledEvent.class);
        orderQueryService.cancelOrderQuery(orderCanceledEvent.getId());
    }

    @KafkaListener(topics = "order-preparing", groupId = "order-query-group")
    public void handleOrderPreparingEvent(String message) {
        OrderPreparingEvent orderPreparingEvent = EventSerializer.deserialize(message, OrderPreparingEvent.class);
        orderQueryService.preparing(orderPreparingEvent.getId());
    }

//    @KafkaListener(topics = "order-requested", groupId = "order-query-group")
//    public void handleOrderRequestedEvent(String message) {
//        OrderRequestedEvent orderRequestedEvent = EventSerializer.deserialize(message, OrderRequestedEvent.class);
//        orderQueryService.requestOrder(orderRequestedEvent.getId());
//    }

    @KafkaListener(topics = "order-deleted", groupId = "order-query-group")
    public void handleOrderDeletedEvent(String message) {
        OrderDeletedEvent orderDeletedEvent = EventSerializer.deserialize(message, OrderDeletedEvent.class);
        orderQueryService.deleteOrder(orderDeletedEvent.getId());
    }







}
