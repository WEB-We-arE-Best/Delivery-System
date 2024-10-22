package com.webest.order.infrastructure.client.delivery.dto;

import com.webest.order.infrastructure.messaging.events.DeliveryStatus;

public record DeliveryResponse(Long id,
                               Long orderId,
                               String riderId,
                               String requestsToRider,
                               DeliveryStatus deliveryStatus,
                               Long storeAddressCode,
                               String storeDetailAddress,
                               Long arrivalAddressCode,
                               String arrivalDetailAddress,
                               Double deliveryFeeAmount) {

                               }