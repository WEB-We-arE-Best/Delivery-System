package com.webest.user.domain.model.vo;

import com.webest.user.presentation.dto.request.UserJoinRequest;
import com.webest.user.presentation.dto.response.UserResponse;
import com.webest.web.common.UserRole;

public record UserDto(
        String userId,
        String password,
        String userName,
        String email,
        String phone,
        UserRole role,       // 권한 체크
        Long addressCode,
        String city,
        String street,
        String district,
        String detailAddress,
        double latitude,
        double longitude
) {


    // AuthDto -> JoinResponse
    public UserResponse to() {
        return new UserResponse(
                this.userId,
                this.userName,
                this.email,
                this.addressCode,
                this.detailAddress,
                this.latitude,
                this.longitude
        );
    }

    // JoinRequest -> AuthDto
    public static UserDto from(UserJoinRequest request, String pw, Long code, double latitude,double longitude ) {
        return new UserDto(
                request.userId(),
                pw,
                request.userName(),
                request.email(),
                request.phone(),
                request.role(),
                code,
                request.city(),
                request.street(),
                request.district(),
                request.detailAddress(),
                latitude,
                longitude
        );
    }

}
