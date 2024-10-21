package com.webest.store.product.infrastructure.kafka.dto;

import com.webest.store.product.domain.model.Product;

public record CartDto(
        String userId,
        Long storeId,
        ProductDto productDto
) {
}
