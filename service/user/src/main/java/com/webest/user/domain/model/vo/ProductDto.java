package com.webest.user.domain.model.vo;

import jakarta.persistence.*;

public record ProductDto(
        Long id,
        Long storeId,
        String name,
        Double price,
        String description
) {
}