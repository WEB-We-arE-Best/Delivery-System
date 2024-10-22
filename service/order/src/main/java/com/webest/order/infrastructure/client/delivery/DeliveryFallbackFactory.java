package com.webest.order.infrastructure.client.delivery;

import com.webest.order.infrastructure.client.store.StoreFallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DeliveryFallbackFactory implements FallbackFactory<DeliveryFallback> {

    @Override
    public DeliveryFallback create(Throwable cause) {
        log.info(cause.toString());
        return new DeliveryFallback(cause);
    }
}
