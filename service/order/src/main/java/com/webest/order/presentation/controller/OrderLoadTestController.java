package com.webest.order.presentation.controller;

import com.webest.order.application.service.OrderLoadTestService;
import com.webest.order.application.service.OrderService;
import com.webest.order.presentation.request.order.OrderCreateRequest;
import com.webest.order.presentation.response.OrderResponse;
import com.webest.web.common.CommonStaticVariable;
import com.webest.web.common.UserRole;
import com.webest.web.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orderLoadTest")
public class OrderLoadTestController {

    private final OrderLoadTestService orderLoadTestService;

    @PostMapping("/feignClient")
    public CommonResponse<OrderResponse> createOrderFeignClient(@RequestHeader(name = CommonStaticVariable.X_USER_ID) String userId,
                                                     @RequestHeader(name = CommonStaticVariable.X_USER_ROLE) UserRole userRole,
                                                     @RequestBody OrderCreateRequest createRequest) {


        return CommonResponse.success(orderLoadTestService.createOrderFeignClient(userId ,userRole, createRequest.toDto()));
    }

    @PostMapping("/kafka")
    public CommonResponse<OrderResponse> createOrderKafka(@RequestHeader(name = CommonStaticVariable.X_USER_ID) String userId,
                                                     @RequestHeader(name = CommonStaticVariable.X_USER_ROLE) UserRole userRole,
                                                     @RequestBody OrderCreateRequest createRequest) {


        return CommonResponse.success(orderLoadTestService.createOrderKafka(userId ,userRole, createRequest.toDto()));
    }

}
