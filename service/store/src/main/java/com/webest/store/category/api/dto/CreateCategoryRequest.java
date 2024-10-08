package com.webest.store.category.api.dto;

import com.webest.store.category.domain.StoreCategory;

public record CreateCategoryRequest(String key, String value) {
    public StoreCategory toEntity() {
        return StoreCategory.of(key, value);
    }
}
