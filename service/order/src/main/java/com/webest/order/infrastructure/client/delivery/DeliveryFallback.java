package com.webest.order.infrastructure.client.delivery;

import com.webest.order.infrastructure.client.delivery.dto.DeliveryCreateRequest;
import com.webest.order.infrastructure.client.delivery.dto.DeliveryResponse;
import com.webest.order.infrastructure.client.store.StoreClient;
import com.webest.web.common.UserRole;
import com.webest.web.response.CommonResponse;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeliveryFallback implements DeliveryClient {

    private final Throwable cause;

    public DeliveryFallback(Throwable cause) {
        this.cause = cause;
    }


    @Override
    public CommonResponse<DeliveryResponse> createDelivery(String userId, UserRole userRole, DeliveryCreateRequest createRequest) {
            return  null;
    }
}
