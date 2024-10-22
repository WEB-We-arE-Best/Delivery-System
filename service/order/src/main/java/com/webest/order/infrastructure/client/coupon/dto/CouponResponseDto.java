package com.webest.order.infrastructure.client.coupon.dto;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.DateType;

import java.time.LocalDateTime;

public record CouponResponseDto(
        Long couponId,
        String content,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Integer duration,
        DateType dateType,
        DiscountType discountType,
        Integer discountValue,
        LocalDateTime createTime,
        Integer maxQuantity,
        Integer issuedQuantity
) {

}
