package com.webest.order.infrastructure.client.delivery;

import com.webest.order.infrastructure.client.delivery.dto.DeliveryCreateRequest;
import com.webest.order.infrastructure.client.delivery.dto.DeliveryResponse;
import com.webest.order.infrastructure.client.store.StoreFallbackFactory;
import com.webest.order.infrastructure.configuration.DeliveryFeignClientConfig;
import com.webest.order.infrastructure.configuration.StoreFeignClientConfig;
import com.webest.web.common.CommonStaticVariable;
import com.webest.web.common.UserRole;
import com.webest.web.response.CommonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "delivery-service",
        url = "http://127.0.0.1:19095",
        configuration = DeliveryFeignClientConfig.class,
        fallbackFactory = DeliveryFallbackFactory.class
)
public interface DeliveryClient {

    @PostMapping("/api/v1/deliveries")
    CommonResponse<DeliveryResponse> createDelivery(@RequestHeader(name = CommonStaticVariable.X_USER_ID) String userId,
                                                           @RequestHeader(name = CommonStaticVariable.X_USER_ROLE) UserRole userRole,
                                                           @RequestBody DeliveryCreateRequest createRequest);



}
