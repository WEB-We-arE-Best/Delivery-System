package com.webest.order.infrastructure.client.store.dto;

public record ProductResponse(
        Long id,
        Long storeId,
        String name,
        Double price,
        String description,
        ProductStatus status
) {

}