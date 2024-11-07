package com.webest.store.product.domain.model.dto;

import com.webest.store.product.domain.model.Product;

public record ProductDto(
    Long id, String name, Double price, String description
) {

    public static ProductDto createProduct(Product product) {
        return new ProductDto(
            product.getId(), product.getName(), product.getPrice(), product.getDescription());
    }

}
