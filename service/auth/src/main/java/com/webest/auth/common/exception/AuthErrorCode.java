package com.webest.auth.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum AuthErrorCode {

    // 400
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저가 없습니다"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "잘못된 비밀번호 입니다."),
    EXIST_USER(HttpStatus.BAD_REQUEST, "이미 존재하는 유저입니다."),


    //
    INVALID_ACCESS_TOKEN(HttpStatus.BAD_REQUEST, "토큰이 잘못되었습니다."),
    REFRESH_TOKEN_END(HttpStatus.BAD_REQUEST, "토큰이 만료되었습니다 로그인을 다시 해주세요."),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "Refresh토큰이 다릅니다. 다시 로그인 해주세요");

    private HttpStatus status;
    private String message;


}
