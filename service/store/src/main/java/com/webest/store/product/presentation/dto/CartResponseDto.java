package com.webest.store.product.presentation.dto;

import com.webest.store.product.domain.model.Cart;

public record CartResponseDto(
    String id,
    String userId,
    Long storeId,
    Long productId,
    String name,
    Double price,
    String description
) {

    public static CartResponseDto from(Cart cart) {
        if (cart == null) {
            return null;
        }

        return new CartResponseDto(cart.getId(), cart.getUserId(), cart.getStoreId(),
            cart.getProductDto().id(), cart.getProductDto().name(), cart.getProductDto().price(),
            cart.getProductDto().description());

    }

}
