package com.webest.coupon.presentation.dtos.response;

import com.webest.coupon.domain.model.CouponUser;
import java.time.LocalDateTime;

public record CouponUserResponseDto(
    Long couponUserId,
    String userId,
    boolean used,
    LocalDateTime expiredTime,
    LocalDateTime usedTime
) {

    public static CouponUserResponseDto from(CouponUser couponUser) {
        return new CouponUserResponseDto(couponUser.getCouponUserId(), couponUser.getUserId(),
            couponUser.isUsed(), couponUser.getExpiredTime(), couponUser.getUsedTime());

    }


}
