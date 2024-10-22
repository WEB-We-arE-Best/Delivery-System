package com.webest.order.presentation.controller;

import com.webest.order.application.service.OrderQueryService;
import com.webest.order.domain.model.OrderStatus;
import com.webest.order.presentation.request.order.OrderSearchRequest;
import com.webest.order.presentation.response.OrderQueryResponse;
import com.webest.web.common.CommonStaticVariable;
import com.webest.web.common.UserRole;
import com.webest.web.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order-queries")
public class OrderQueryController {

    private final OrderQueryService orderQueryService;

    @GetMapping("/{orderId}")
    public CommonResponse<OrderQueryResponse> getOrder(@RequestHeader(name = CommonStaticVariable.X_USER_ID) String userId,
                                                       @RequestHeader(name = CommonStaticVariable.X_USER_ROLE) UserRole userRole,
                                                       @PathVariable(name = "orderId") Long orderId) {

        return CommonResponse.success(orderQueryService.getOrderQuery(userId, userRole, orderId));
    }

    @GetMapping
    public CommonResponse<?> getAllOrders(@RequestHeader(name = CommonStaticVariable.X_USER_ID) String userId,
                                          @RequestHeader(name = CommonStaticVariable.X_USER_ROLE) UserRole userRole) {

        return CommonResponse.success(orderQueryService.getAllOrderQueries(userId, userRole));
    }

    @GetMapping("/search")
    public CommonResponse<?> searchOrders(@RequestHeader(name = CommonStaticVariable.X_USER_ID) String userId,
                                          @RequestHeader(name = CommonStaticVariable.X_USER_ROLE) UserRole userRole,
                                          @RequestParam(required = false) Long orderId,
                                          @RequestParam(required = false) Long storeId,
                                          @RequestParam(required = false) Long paymentId,
                                          @RequestParam(required = false) Long couponId,
                                          @RequestParam(required = false) String searchUserId,
                                          @RequestParam(required = false) OrderStatus orderStatus,
                                          @RequestParam(required = false) Boolean isRequest,
                                          @RequestParam(required = false) String requestsToStore,
                                          @RequestParam(required = false) String requestsToRider,
                                          @RequestParam(required = false) Integer totalQuantity,
                                          @RequestParam(required = false) Double totalProductPrice,
                                          @RequestParam(required = false) Double couponAppliedAmount,
                                          @RequestParam(required = false) Double deliveryTipAmount,
                                          @RequestParam(required = false) Double totalPaymentPrice,
                                          @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "20") int size) {

        PageRequest pageRequest = PageRequest.of(page - 1, size);

        // OrderSearchRequest 객체 수동 생성
        OrderSearchRequest orderSearchRequest = new OrderSearchRequest(
                orderId,
                storeId,
                paymentId,
                couponId,
                searchUserId,
                orderStatus,
                isRequest,
                requestsToStore,
                requestsToRider,
                totalQuantity,
                totalProductPrice,
                couponAppliedAmount,
                deliveryTipAmount,
                totalPaymentPrice
        );

        return CommonResponse.success(orderQueryService.searchOrders(userId, userRole, orderSearchRequest.toDto(), pageRequest));
    }

}
