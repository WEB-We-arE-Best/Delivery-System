package com.webest.coupon.common.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        // 현재 HttpServletRequest 가져오기
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = requestAttributes.getRequest();

            // JWT 토큰이 포함된 헤더 가져오기
            String jwtToken = request.getHeader("Authorization");

            // FeignClient 요청에 JWT 토큰을 포함시키기
            if (jwtToken != null) {
                requestTemplate.header("Authorization", jwtToken);
            }
        }
    }
}
