package com.webest.order.infrastructure.messaging.topic;


import lombok.Getter;

@Getter
public enum OrderTopic {

    CREATED("order-created"),
    PAYMENT_COMPLETED("order-payment-completed"),
    UPDATED("order-updated"),
    CANCELED("order-canceled"),
    PREPARING("order-preparing"),
    COMPLETED("order-completed"),
    REQUEST("order-requested"),
    DELETED("order-deleted"),
    ROLLBACK("order-rollback");

    private final String topic;

    OrderTopic(String topic) {
        this.topic = topic;
    }

}
