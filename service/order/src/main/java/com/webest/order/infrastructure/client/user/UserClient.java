package com.webest.order.infrastructure.client.user;

import com.webest.order.infrastructure.client.user.dto.UserResponse;
import com.webest.order.infrastructure.configuration.UserFeignClientConfig;
import com.webest.web.response.CommonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "user-service",
        url = "http://127.0.0.1:19092",
        configuration = UserFeignClientConfig.class,
        fallbackFactory = UserFallbackFactory.class
)
public interface UserClient{

    @GetMapping("/api/users/myPage")
    CommonResponse<UserResponse> getUser(
            @RequestHeader("X-UserId") String userId
    );

}
