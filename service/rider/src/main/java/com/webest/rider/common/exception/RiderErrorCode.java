package com.webest.rider.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum RiderErrorCode {

    // 400
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "잘못된 비밀번호 입니다."),
    INVALID_PHONE_NUMBER_INPUT(HttpStatus.BAD_REQUEST, "잘못된 휴대전화 입력입니다."),

    EXIST_PHONE_NUMBER(HttpStatus.BAD_REQUEST, "이미 존재하는 휴대전화입니다."),
    EXIST_USER_ID(HttpStatus.BAD_REQUEST, "이미 존재하는 유저아이디 입니다."),

    // 403
    NOT_ADMIN(HttpStatus.FORBIDDEN, "권한이 없습니다.."),
    NOT_RIDER(HttpStatus.FORBIDDEN, "라이더만 접근 가능합니다."),
    NOT_SELF_USER(HttpStatus.FORBIDDEN, "자기 자신의 정보만 조회 가능합니다."),


    // 404
    RIDER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 라이더 정보입니다."),
    ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 주소 정보입니다."),

    // 500
    DB_CONVERTING_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DB 타입 변환 과정에서 오류 발생"),
    ;

    private HttpStatus status;
    private String message;


}
