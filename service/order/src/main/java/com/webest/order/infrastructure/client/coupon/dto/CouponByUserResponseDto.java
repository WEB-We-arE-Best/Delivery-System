package com.webest.order.infrastructure.client.coupon.dto;

import java.time.LocalDateTime;

public record CouponByUserResponseDto(
        Long userCouponId,
        Long couponId,
        String content,
        LocalDateTime expiredTime,
        Boolean used
) {

}
