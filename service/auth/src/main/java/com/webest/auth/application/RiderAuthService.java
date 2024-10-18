package com.webest.auth.application;

import com.webest.auth.common.exception.AuthErrorCode;
import com.webest.auth.common.exception.AuthException;
import com.webest.auth.domain.model.TokenStatus;
import com.webest.auth.domain.model.vo.RefreshTokenDto;
import com.webest.auth.infrastructure.core.RiderClient;
import com.webest.auth.infrastructure.core.dto.RiderResponseDto;
import com.webest.auth.infrastructure.redis.RedisUtil;
import com.webest.auth.presentation.dto.request.rider.RiderAuthRequestDto;
import com.webest.web.common.UserRole;
import com.webest.web.response.CommonResponse;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RiderAuthService {

    private final RiderClient riderClient;
    private final RedisUtil redisUtil;
    private final JwtTokenService jwtTokenService;
    private String tokenTime;

    public RiderAuthService(RiderClient riderClient, RedisUtil redisUtil,
        JwtTokenService jwtTokenService, @Value("${token.expiration-time}") String tokenTime) {
        this.riderClient = riderClient;
        this.redisUtil = redisUtil;
        this.jwtTokenService = jwtTokenService;
        this.tokenTime = tokenTime;
    }

    public String riderAuth(RiderAuthRequestDto requestDto) {
        // 라이더 정보 갖고 온다.
        CommonResponse<RiderResponseDto> riderResponse;
        try {
            riderResponse = riderClient.authRider(
                requestDto);
        } catch (Exception e) {
            if (e instanceof FeignException.NotFound) {
                throw new AuthException(AuthErrorCode.USER_NOT_FOUND);
            } else if (e instanceof FeignException.BadRequest) {
                throw new AuthException(AuthErrorCode.INVALID_PASSWORD);
            } else {
                throw new IllegalArgumentException(e.getMessage());
            }
        }

        // 엑세스 토큰 생성 및 header 추가 , 만료시간 10분
        String accessToken = jwtTokenService.createToken(riderResponse.getData().userId(),
            UserRole.RIDER, Long.parseLong(tokenTime));

        // 리프레시 토큰 생성 및 redis 저장 , 만료시간 1시간
        String refreshToken = jwtTokenService.createToken(riderResponse.getData().userId(),
            UserRole.RIDER, Long.parseLong(tokenTime) * 6);
        RefreshTokenDto refreshTokenDto = new RefreshTokenDto(riderResponse.getData().userId(),
            refreshToken, TokenStatus.ACTIVE);
        redisUtil.setDataRefreshToken(refreshTokenDto);

        return accessToken;
    }

}
