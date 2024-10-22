package com.webest.delivery.infrastructure.messaging.topic;

import lombok.Getter;

@Getter
public enum DeliveryTopic {

    CREATED("delivery-created"),
    UPDATED("delivery-updated"),
    DISPATCHED("delivery-dispatched"),
    DEPARTED("delivery-departed"),
    COMPLETED("delivery-completed"),
    CANCELED("delivery-canceled"),
    ROLLBACK("delivery-rollback");


    private final String topic;

    DeliveryTopic(String topic) {
        this.topic = topic;
    }

}
