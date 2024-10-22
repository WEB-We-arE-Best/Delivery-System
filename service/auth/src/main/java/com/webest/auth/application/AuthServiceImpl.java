package com.webest.auth.application;

import com.webest.auth.common.exception.AuthErrorCode;
import com.webest.auth.common.exception.AuthException;
import com.webest.auth.domain.model.Auth;
import com.webest.auth.domain.model.TokenStatus;
import com.webest.auth.domain.model.UserRoleCustom;
import com.webest.auth.domain.model.vo.AuthDto;
import com.webest.auth.domain.model.vo.RefreshTokenDto;
import com.webest.auth.domain.repository.AuthRepository;
import com.webest.auth.infrastructure.core.RiderClient;
import com.webest.auth.infrastructure.core.UserClient;
import com.webest.auth.infrastructure.redis.RedisUtil;
import com.webest.auth.presentation.dto.request.RefreshRequest;
import com.webest.auth.presentation.dto.request.rider.RiderCreateRequestDto;
import com.webest.auth.presentation.dto.request.UserJoinRequest;
import com.webest.auth.presentation.dto.response.JoinResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthRepository authRepository;
    private final UserClient userClient;
    private final RiderClient riderClient;
    private final RedisUtil redisUtil;
    private final JwtTokenService jwtTokenService;

    @Value("${token.secret-key}")
    private String secretKey;

    @Value("${token.expiration-time}")
    private String tokenTime;

    @Override
    public JoinResponse create(UserJoinRequest request) {
        JoinResponse response = userClient.createUser(request);

        return response;
    }

    @Override
    public String createRider(RiderCreateRequestDto requestDto) {
        try {
            return riderClient.createRider(requestDto).getData();
        } catch (Exception e) {
            if (e instanceof FeignException.NotFound) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            } else if (e instanceof FeignException.BadRequest) {
                throw new AuthException(AuthErrorCode.EXIST_USER);
            } else {
                throw e;
            }
        }
    }

    // 토큰 재발행
    @Override
    public String refreshToken(RefreshRequest request) {

        // Redis에 저장된 토큰 가져옴
        if(redisUtil.getRefreshToken(request.userId()) != null) {
            RefreshTokenDto dto = redisUtil.getRefreshToken(request.userId());
            AuthDto userDetails = getUserDetailsByUserId(request.userId());

            // 입력받은 refresh토큰과 redis에 저장된 토큰이 같다면(refresh 토큰 탈취 방지)
            if(request.refreshToken().equals(dto.token())){
                return jwtTokenService.createToken(userDetails.userId(), userDetails.role(),
                    Long.parseLong(tokenTime));
            }else{
                // TODO :: 강제 로그아웃 기능 추가
                throw new AuthException(AuthErrorCode.INVALID_REFRESH_TOKEN);
            }
        }else {     // RefreshToken 만료
            throw new AuthException(AuthErrorCode.REFRESH_TOKEN_END);
        }

    }

    // 토큰 비활성화
    @Override
    public void logout(String userId) {
        RefreshTokenDto dto = redisUtil.getRefreshToken(userId);
        RefreshTokenDto refreshTokenDto = new RefreshTokenDto(userId,dto.token(), TokenStatus.DEACTIVATE);
        redisUtil.setDataRefreshToken(refreshTokenDto);
    }

    @Override
    public AuthDto getUserDetailsByUserId(String userName) {
        Auth auth = authRepository.findByUserId(userName)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));
        return auth.to();
    }


    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Auth auth = authRepository.findByUserId(userName)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));

        // UserRole을 List<GrantedAuthority>로 변환
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new UserRoleCustom(auth.getRole())); // UserRole을 GrantedAuthority로 추가

        return new User(auth.getUserId(), auth.getPassword(), true,true,true,true,authorities);    // new ArrayList -> 권한 추가
    }
}
