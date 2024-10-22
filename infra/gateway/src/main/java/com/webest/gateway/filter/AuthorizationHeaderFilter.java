package com.webest.gateway.filter;

import com.webest.gateway.redis.RedisUtil;
import com.webest.gateway.vo.RefreshTokenDto;
import com.webest.gateway.vo.TokenStatus;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends
    AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    private final RedisUtil redisUtil;
    @Value("${token.secret-key}")
    private String secretKey;


    public AuthorizationHeaderFilter(RedisUtil redisUtil) {
        super(Config.class);
        this.redisUtil = redisUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
//            if (path.equals("/api/auth/signIn") || path.equals("/auth/signUp")) {
//                return chain.filter(exchange);  // /signIn,signUp 경로는 필터를 적용하지 않음
//            }

            if (exchange.getRequest().getURI().getPath().endsWith("api-docs")) {
                return chain.filter(exchange);
            }

            ServerHttpRequest request = exchange.getRequest();

            // Header에 AUTHORIZATION에 관련된 값이 있는지 확인
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "no authorization header", HttpStatus.UNAUTHORIZED);
            }

            String token = extractToken(exchange);

            Claims claims;
            try {
                claims = extractClaims(token);
            } catch (ExpiredJwtException e) {
                return onError(exchange, "토큰 시간 만료이 만료되었습니다.", HttpStatus.UNAUTHORIZED);
            } catch (Exception e) {
                return onError(exchange, "잘못된 토큰입니다.", HttpStatus.UNAUTHORIZED);
            }

            // access 토큰 만료 시간 검사
            Date expiration = claims.getExpiration();
            if (expiration.before(new Date())) {
                return onError(exchange, "토큰 시간 만료, RefreshToken을 같이 보내주세요",
                    HttpStatus.UNAUTHORIZED);
            }

            // Redis에 저장되어 있는 토큰 값 가져옴
            RefreshTokenDto dto;
            try {
                dto = redisUtil.getRefreshToken(claims.get("userId").toString());
            } catch (Exception e) {
                return onError(exchange, "Redis에서 값을 갖고 올 때 문제 발생",
                    HttpStatus.UNAUTHORIZED);
            }

            // 현재 토큰의 상태가 비활성화 일경우(로그아웃 상태)
            if (dto == null || dto.status() == TokenStatus.DEACTIVATE) {
                return onError(exchange, "로그아웃 된 계정입니다 재로그인 해주세요", HttpStatus.UNAUTHORIZED);
            }

            if (!validateToken(claims, exchange)) {
                return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
            }

            return chain.filter(exchange);
        });
    }

    // 토큰 분리
    private String extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    // 토큰 암호화 제거
    private Claims extractClaims(String token) {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        var secretKeyBytes = Keys.hmacShaKeyFor(bytes);
//            SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));

        Jws<Claims> claimsJws = Jwts.parserBuilder()
            .setSigningKey(secretKeyBytes)
            .build()
            .parseClaimsJws(token);

        return claimsJws.getBody();
    }

    // 토큰 검증
    // ServerWebExchange : 현재 HTTP 요청/응답 교환을 나타내는 객체로 요청 헤더를 수정할 수 있음
    private boolean validateToken(Claims claims, ServerWebExchange exchange) {
        try {

            log.info("#####payload :: " + claims.toString());

            // 요청 헤더 수정 (Userid,role 추가)
            exchange.getRequest().mutate()
                .header("X-UserId", claims.get("userId").toString())
                .header("X-Role", claims.get("role").toString())
                .build();
            // 추가적인 검증 로직 (예: 토큰 만료 여부 확인 등)을 여기에 추가할 수 있습니다.
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Mono, Flux -> Spring WebFlux
    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        // Content-Type 설정
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // Body에 추가할 문자열 생성
        String jsonResponse = String.format("{\"error\": \"%s\"}", message);

        // DataBuffer에 JSON 문자열 담기
        DataBuffer buffer = response.bufferFactory()
            .wrap(jsonResponse.getBytes(StandardCharsets.UTF_8));

        log.error(message);

        // 응답에 Body 쓰기
        return response.writeWith(Mono.just(buffer));
    }

    public static class Config {

    }
}
