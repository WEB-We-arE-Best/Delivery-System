package com.webest.coupon.common.exception;

import org.springframework.http.HttpStatus;

public enum CouponErrorCode {
    // 400
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 입력입니다."),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 유저입니다."),
    NOT_OWNED_COUPON(HttpStatus.BAD_REQUEST, "쿠폰을 소유하고 있지 않습니다"),

    /**
     * 쿠폰의 Validation
     */
    STARTTIME_AFTER_ENDTIME(HttpStatus.BAD_REQUEST, "시작 날짜가 끝 날짜를 넘을 수 없습니다."),
    COUPON_NOT_ISSUE_PERIOD(HttpStatus.BAD_REQUEST, "쿠폰 발급 가능 기간이 아닙니다"),
    COUPON_OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "쿠폰 발급 수량이 모두 소진되었습니다"),
    NOT_FOUND_IN_POSITION(HttpStatus.BAD_REQUEST, "현재 발급 대기 중이 아닌 유저입니다."),
    COUPON_CLOSED(HttpStatus.BAD_REQUEST, "쿠폰 발급이 현재 중지되어 있습니다."),
    COUPON_PERCENTAGE_OVER_90(HttpStatus.BAD_REQUEST, "퍼센트 쿠폰은 90을 넘길 수 없습니다."),
    CHECK_UNDER_ZERO(HttpStatus.BAD_REQUEST, "기간과 발급 최대 수량은 0 이상이어야 합니다.."),

    // 403
    NOT_ADMIN(HttpStatus.FORBIDDEN, "권한이 없습니다.."),
    NOT_SELF_USER(HttpStatus.FORBIDDEN, "자기 자신의 쿠폰만 조회 가능합니다."),


    // 404
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 쿠폰 정보입니다."),

    // 500
    DB_CONVERTING_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DB 타입 변환 과정에서 오류 발생"),
    LOCK_WAITING_TIMEOUT(HttpStatus.INTERNAL_SERVER_ERROR, "락의 대기시간 초과"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류"),

    ;

    private HttpStatus status;
    private String message;

    CouponErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
