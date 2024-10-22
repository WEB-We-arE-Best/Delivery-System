package com.webest.order.domain.service;

import com.webest.order.domain.exception.ErrorCode;
import com.webest.order.domain.exception.OrderException;
import com.webest.order.infrastructure.client.coupon.dto.CouponByUserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderCouponDomainService {



    public void validateCouponUsage(Long couponId, List<CouponByUserResponseDto> usedCoupons) {
        // 이미 사용된 쿠폰 리스트에서 couponId와 동일한 값이 있는지 확인
        boolean isCouponAlreadyUsed = usedCoupons.stream()
                .anyMatch(coupon -> coupon.couponId().equals(couponId));

        if (isCouponAlreadyUsed) {
            // 중복된 쿠폰 사용시 예외 발생
            throw new OrderException(ErrorCode.COUPON_ALREADY_USED_EXCEPTION);
        }
    }
}
