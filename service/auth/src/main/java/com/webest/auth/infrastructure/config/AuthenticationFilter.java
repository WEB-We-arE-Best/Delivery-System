package com.webest.auth.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webest.auth.application.AuthService;
import com.webest.auth.application.JwtTokenService;
import com.webest.auth.domain.model.TokenStatus;
import com.webest.auth.domain.model.vo.AuthDto;
import com.webest.auth.domain.model.vo.RefreshTokenDto;
import com.webest.auth.infrastructure.redis.RedisUtil;
import com.webest.auth.presentation.dto.request.LoginRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final String accessTokenTime;
    private final RedisUtil redisUtil;
    private final AuthService authService;
    private final JwtTokenService jwtTokenService;

    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    private Key key;

    public AuthenticationFilter(AuthenticationManager authenticationManager, AuthService authService, String tokenTime, RedisUtil redisUtil, JwtTokenService jwtTokenService) {
        super(authenticationManager);
        this.authService = authService;
        this.accessTokenTime = tokenTime;
        this.redisUtil = redisUtil;
        this.jwtTokenService = jwtTokenService;
    }

    /*
        1. 유저가 현재 로그인한 데이터값을 LoginRequest값과 맵핑 시켜준다.
        2. 이후 getAuthenticationManager().authenticate를 통해 ServiceImpl에 loadUserByUsername를 통해 검증 진행
        3. 해당 검증이 정상이라면 successfulAuthentication를 통해 결과값 반환
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request
            , HttpServletResponse response) throws AuthenticationException {

        // 특정 엔드포인트를 제외
        if (request.getRequestURI().startsWith("/api/auth/refresh")) {
            return null; // 필터를 건너뜀
        }


        try{
            LoginRequest loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);     // 로그인 데이터를 LoginRequest 형태로 맵핑
            // 인증 정보
            // 유저에게 입력받은 email과 password를 UsernamePasswordAuthenticationToken를 입혀서 인증검사를 대신 해주는 getAuthenticationManager로 보내 검사 진행
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.userId(),
                            loginRequest.password(),
                            new ArrayList<>())
            );

        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }


    // 인증 성공했을때 반환 값
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {


        String userName = (((User)authResult.getPrincipal()).getUsername());
        AuthDto userDetails = authService.getUserDetailsByUserId(userName);

        // 엑세스 토큰 생성 및 header 추가 , 만료시간 10분
        String accessToken = jwtTokenService.createToken(userDetails.userId(), userDetails.role(),
            Long.parseLong(accessTokenTime));
        response.addHeader("Authorization", "Bearer " + accessToken);

        // 리프레시 토큰 생성 및 redis 저장 , 만료시간 1시간
        String refreshToken = jwtTokenService.createToken(userDetails.userId(), userDetails.role(),
            Long.parseLong(accessTokenTime) * 6);
        RefreshTokenDto refreshTokenDto = new RefreshTokenDto(userDetails.userId(),refreshToken, TokenStatus.ACTIVE);
        redisUtil.setDataRefreshToken(refreshTokenDto);
    }

}
