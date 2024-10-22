package com.webest.order.infrastructure.client.coupon;

import com.webest.order.infrastructure.client.coupon.dto.CouponByUserResponseDto;
import com.webest.order.infrastructure.client.coupon.dto.CouponUsedRequestDto;
import com.webest.order.infrastructure.client.store.StoreFallbackFactory;
import com.webest.order.infrastructure.configuration.CouponFeignClientConfig;
import com.webest.order.infrastructure.configuration.StoreFeignClientConfig;
import com.webest.web.common.CommonStaticVariable;
import com.webest.web.common.UserRole;
import com.webest.web.response.CommonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "coupon-service",
        configuration = CouponFeignClientConfig.class,
        fallbackFactory = CouponFallbackFactory.class
)
public interface CouponClient {

    /**
     * 유저 소유한 쿠폰 목록 조회 (자기 자신만 조회 가능)
     */
    @GetMapping("/api/v1/coupons/users")
    CommonResponse<List<CouponByUserResponseDto>> findCouponByUserId(
            @RequestHeader(name = CommonStaticVariable.X_USER_ID) String xUserId,
            @RequestHeader(name = CommonStaticVariable.X_USER_ROLE) UserRole userRole,
            @RequestParam(value = "used", required = false) Boolean used
    );

}
