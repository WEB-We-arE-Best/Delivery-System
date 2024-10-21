package com.webest.user.domain.model.vo;


import java.util.Set;

public record ShoppingCartDto(
        String userId,
        Long storeId,
        Set<OrderProductDto> product
) {
}
