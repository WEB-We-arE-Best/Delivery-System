package com.webest.order.infrastructure.client.coupon;

import com.webest.order.infrastructure.client.store.StoreFallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CouponFallbackFactory implements FallbackFactory<CouponFallback> {

    @Override
    public CouponFallback create(Throwable cause) {
        log.info(cause.toString());
        return new CouponFallback(cause);
    }
}
