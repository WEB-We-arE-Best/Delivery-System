package com.webest.order.application.service;

import com.webest.order.domain.events.*;
import com.webest.order.infrastructure.serialization.EventSerializer;
import com.webest.order.infrastructure.messaging.topic.OrderTopic;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    // 주문 생성시 발생하는 이벤트
    public void publishOrderCreatedEvent(OrderCreatedEvent event) {
        kafkaTemplate.send(OrderTopic.CREATED.getTopic(), EventSerializer.serialize(event));
    }

    // 주문 완료시 발생하는 이벤트 (결제 완료까지 되었을 때)
    public void publishOrderPaymentCompletedEvent(OrderPaymentCompletedEvent event) {
        kafkaTemplate.send(OrderTopic.PAYMENT_COMPLETED.getTopic(), EventSerializer.serialize(event));
    }


    // 주문 수정시 발생하는 이벤트
    public void publishOrderUpdatedEvent(OrderUpdatedEvent event) {
        kafkaTemplate.send(OrderTopic.UPDATED.getTopic(), EventSerializer.serialize(event));
    }

    // 주문 취소시 발생하는 이벤트
    public void publishOrderCanceledEvent(OrderCanceledEvent event) {
        kafkaTemplate.send(OrderTopic.CANCELED.getTopic(), EventSerializer.serialize(event));
    }

    // 주문이 준비중(PREPARING) 으로 변경되었을때 발생하는 이벤트
    public void publishOrderPreparingEvent(OrderPreparingEvent event) {
        kafkaTemplate.send(OrderTopic.PREPARING.getTopic(), EventSerializer.serialize(event));
    }

    // 주문 -> 배달로 요청시 발생하는 이벤트
    public void publishOrderRequestEvent(OrderRequestedEvent event) {
        kafkaTemplate.send(OrderTopic.REQUEST.getTopic(), EventSerializer.serialize(event));
    }

    // 주문 완료시 발생하는 이벤트 (결제 완료까지 되었을 때)
    public void publishOrderCompletedEvent(OrderCompletedEvent event) {
        kafkaTemplate.send(OrderTopic.PAYMENT_COMPLETED.getTopic(), EventSerializer.serialize(event));
    }

    // 주문 삭제시 발생하는 이벤트
    public void publishOrderDeletedEvent(OrderDeletedEvent event) {
        kafkaTemplate.send(OrderTopic.DELETED.getTopic(), EventSerializer.serialize(event));
    }

    // 주문 롤백시 발생하는 이벤트
    public void publishOrderRollbackEvent(OrderRollbackEvent event) {
        kafkaTemplate.send(OrderTopic.ROLLBACK.getTopic(), EventSerializer.serialize(event));
    }





}
