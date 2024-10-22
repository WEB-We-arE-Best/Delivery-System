package com.webest.rider.application.feign.dtos;

import com.webest.rider.common.constant.DeliveryStatus;

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
