package com.webest.order.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    // ORDER 관련 에러
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."),
    ORDER_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 주문입니다."),
    ORDER_CANNOT_BE_MODIFIED(HttpStatus.FORBIDDEN, "주문을 수정할 수 없습니다."),
    INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "주문 상태가 잘못되었습니다."),


    // ORDER_PRODUCT 관련 에러
    ORDER_PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "주문상품을 찾을 수 없습니다.");

    private HttpStatus status;
    private String message;
}
