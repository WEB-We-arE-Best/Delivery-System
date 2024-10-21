package com.webest.store.product.infrastructure.kafka.dto;

public record ProductDto(
        Long id, Long storeId, String name, Double price,String description
) {

}
