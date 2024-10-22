package com.webest.user.domain.model.vo;


public record OrderProductDto(
        Long productId,
        Integer quantity,
        Double price
) {
}
