package com.webest.order.domain.service;

import com.webest.order.infrastructure.client.coupon.CouponClient;
import com.webest.order.infrastructure.client.coupon.dto.CouponByUserResponseDto;
import com.webest.order.infrastructure.client.store.StoreClient;
import com.webest.web.common.CommonStaticVariable;
import com.webest.web.common.UserRole;
import com.webest.web.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponClient couponClient;

    public List<CouponByUserResponseDto> findCouponByUserId(String xUserId,
                                                            UserRole userRole,
                                                            Boolean used) {
       return couponClient.findCouponByUserId(xUserId, userRole, used).getData();
    }

}
