package com.webest.delivery.application.service;

import com.webest.delivery.domain.events.*;
import com.webest.delivery.infrastructure.messaging.events.OrderCanceledEvent;
import com.webest.delivery.infrastructure.messaging.events.OrderRequestedEvent;
import com.webest.delivery.infrastructure.messaging.topic.DeliveryTopic;
import com.webest.delivery.infrastructure.serialization.EventSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryEventService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    // 배달 생성시 발생하는 이벤트
    public void publishDeliveryCreatedEvent(DeliveryCreatedEvent event) {
        kafkaTemplate.send(DeliveryTopic.CREATED.getTopic(), EventSerializer.serialize(event));
    }

    // 배달 수정시 발생하는 이벤트
    public void publishDeliveryUpdatedEvent(DeliveryUpdatedEvent event) {
        kafkaTemplate.send(DeliveryTopic.UPDATED.getTopic(), EventSerializer.serialize(event));
    }

    // 배달 취소시 발생하는 이벤트
    public void publishDeliveryCanceledEvent(DeliveryCanceledEvent event) {
        kafkaTemplate.send(DeliveryTopic.CANCELED.getTopic(), EventSerializer.serialize(event));
    }

    // 배차시 발생하는 이벤트
    public void publishDeliveryDispatchedEvent(DeliveryDispatchedEvent event) {
        kafkaTemplate.send(DeliveryTopic.DISPATCHED.getTopic(), EventSerializer.serialize(event));
    }

    // 출발시 발생하는 이벤트
    public void publishDeliveryDepartedEvent(DeliveryDepartedEvent event) {
        kafkaTemplate.send(DeliveryTopic.DEPARTED.getTopic(), EventSerializer.serialize(event));
    }

    // 완료시 발생하는 이벤트
    public void publishDeliveryCompletedEvent(DeliveryCompletedEvent event) {
        kafkaTemplate.send(DeliveryTopic.COMPLETED.getTopic(), EventSerializer.serialize(event));
    }

    // 롤백 이벤트 발행
    public void publishDeliveryRollbackEvent(DeliveryRollbackEvent event) {
        kafkaTemplate.send(DeliveryTopic.ROLLBACK.getTopic(), EventSerializer.serialize(event));
    }


}
