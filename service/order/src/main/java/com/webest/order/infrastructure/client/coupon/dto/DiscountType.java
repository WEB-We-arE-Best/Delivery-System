package com.webest.order.infrastructure.client.coupon.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum DiscountType {
    FIXED("고정"), // 고정 금액 할인
    PERCENTAGE("비율"); // 비율 할인

    String type;

    DiscountType(String type) {
        this.type = type;
    }

    @JsonCreator
    public static DiscountType from(String value) {
        for (DiscountType type : DiscountType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid value for DiscountType: " + value);
    }

}
