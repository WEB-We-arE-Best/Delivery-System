package com.webest.rider.application.feign;

import com.webest.rider.application.feign.dtos.DeliveryResponse;
import com.webest.rider.application.feign.dtos.DeliverySearchRequest;
import com.webest.web.response.CommonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "delivery-service"
)
public interface DeliveryClient {

    @GetMapping("/api/v1/deliveries/search")
    public CommonResponse<Page<DeliveryResponse>> findDeliveryListByCode(
        @RequestBody DeliverySearchRequest request);

}
