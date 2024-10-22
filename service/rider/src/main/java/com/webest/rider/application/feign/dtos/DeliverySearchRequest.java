package com.webest.rider.application.feign.dtos;

import com.webest.rider.common.constant.DeliveryStatus;

public record DeliverySearchRequest(Long orderId,
                                    String riderId,
                                    String requestsToRider,
                                    DeliveryStatus deliveryStatus,
                                    Long storeAddressCode,
                                    String storeDetailAddress,
                                    Long arrivalAddressCode,
                                    String arrivalDetailAddress,
                                    Double deliveryFeeAmount) {

    public DeliverySearchRequest(DeliveryStatus deliveryStatus, String storeDetailAddress,
        Long arrivalAddressCode) {
        this(null, null, null, deliveryStatus, null, storeDetailAddress, arrivalAddressCode, null,
            null);
    }
}

