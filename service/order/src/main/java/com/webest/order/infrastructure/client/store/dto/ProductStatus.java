package com.webest.order.infrastructure.client.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductStatus {
    AVAILABLE("주문 가능"), SOLD_OUT("품절");
    private final String description;
}