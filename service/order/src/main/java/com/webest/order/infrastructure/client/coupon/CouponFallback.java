package com.webest.order.infrastructure.client.coupon;

import com.webest.order.infrastructure.client.coupon.dto.CouponByUserResponseDto;
import com.webest.order.infrastructure.client.coupon.dto.CouponUsedRequestDto;
import com.webest.web.common.UserRole;
import com.webest.web.response.CommonResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class CouponFallback implements CouponClient{

    private final Throwable cause;

    public CouponFallback(Throwable cause) {
        this.cause = cause;
    }


    @Override
    public CommonResponse<List<CouponByUserResponseDto>> findCouponByUserId(String xUserId, UserRole userRole, Boolean used) {
        if (cause instanceof FeignException.NotFound) {
            log.error("Not found error");
        }
        log.error("Failed to get user {}", xUserId);
        return new CommonResponse<>(404, "쿠폰 데이터를 가져오지 못했습니다.", null);
    }
}
