package com.webest.coupon.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.webest.coupon.common.exception.CouponErrorCode;
import com.webest.coupon.common.exception.CouponException;
import java.util.stream.Stream;

public enum DateType {
    HOUR, // 시간
    DAY, // 날짜
    MONTH; // 달

    @JsonCreator
    public static DateType parsing(String inputValue) {
        return Stream.of(DateType.values())
            .filter(dateType -> dateType.toString().equals(inputValue.toUpperCase()))
            .findFirst()
            .orElseThrow(
                () -> new CouponException(CouponErrorCode.INVALID_INPUT)
            );
    }
}
