package com.webest.coupon.application;

import com.webest.coupon.presentation.dtos.response.CouponByUserResponseDto;
import com.webest.coupon.presentation.dtos.response.CouponUserResponseDto;
import java.util.List;

public interface CouponUserService {

    public List<CouponByUserResponseDto> findCouponListByUser(String userId, Boolean used);

    public CouponUserResponseDto findUserCouponById(String userId, Long userCouponId);

    public boolean issueCouponToUser(Long couponId, String userId);

    public boolean issueCouponWithQueue(Long couponId, String userId);

    public Long checkCurrentOffsetInWaiting(Long couponId, String userId);

    public boolean useCouponByUser(Long userCouponId, Long couponId, String userId);

}
