package com.webest.store.category.api.dto;

import com.webest.store.category.domain.StoreCategory;

public record UpdateCategoryRequest(Long id, String value) {
}
