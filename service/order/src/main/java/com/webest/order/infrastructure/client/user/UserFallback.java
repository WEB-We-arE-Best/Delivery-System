package com.webest.order.infrastructure.client.user;

import com.webest.order.infrastructure.client.user.dto.UserResponse;
import com.webest.web.response.CommonResponse;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserFallback implements UserClient {


    private final Throwable cause;

    public UserFallback(Throwable cause) {
        this.cause = cause;
    }


    @Override
    public CommonResponse<UserResponse> getUser(String userId) {
        if (cause instanceof FeignException.NotFound) {
            log.error("Not found error");
        }
        log.error("Failed to get user {}", userId);
        return new CommonResponse<>(404, "유저 데이터를 가져오지 못했습니다.", null);
    }
}
