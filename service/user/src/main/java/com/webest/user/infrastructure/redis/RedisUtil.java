package com.webest.user.infrastructure.redis;

import com.webest.user.domain.model.vo.ShoppingCartDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String KEY_NAME = "CART:";

    // RefreshToken 전용
    public void setDataShoppingCart(ShoppingCartDto dto){//key - 유저 데이터,value - refreshToken 지정된 시간(duration) 후에 데이터가 만료되도록 설정하는 메서드
        String key = KEY_NAME + dto.userId();
        // 만료시간 1일
        redisTemplate.opsForValue().set(key,dto,1, TimeUnit.DAYS);
    }

    // RefreshToken 전용
    public ShoppingCartDto getShoppingCart(String userId) {
        String key = KEY_NAME + userId;
        return (ShoppingCartDto) redisTemplate.opsForValue().get(key);
    }

}