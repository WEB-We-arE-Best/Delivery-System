package com.webest.order.infrastructure.client.coupon.dto;

import jakarta.validation.constraints.NotNull;

public record CouponUsedRequestDto(
        @NotNull
        Long userCouponId,
        @NotNull
        String userId,
        @NotNull
        Long couponId
) {

}
