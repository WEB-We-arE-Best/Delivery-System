package com.webest.coupon.infrastructure.config;

import com.webest.coupon.common.exception.CustomFeignErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomFeignErrorDecoder();
    }

//    @Bean
//    public RequestInterceptor feignClientInterceptor() {
//        return new FeignClientInterceptor();
//    }

}

