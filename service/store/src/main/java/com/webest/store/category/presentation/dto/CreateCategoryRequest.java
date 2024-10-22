package com.webest.store.category.presentation.dto;

import com.webest.store.category.domain.model.StoreCategory;
import jakarta.validation.constraints.*;


public record CreateCategoryRequest(
        @NotBlank(message = "카테고리 KEY를 입력해주세요.")
        @Size(max = 30, message = "카테고리 KEY는 최대 30자까지 입력할 수 있습니다.")
        String key,
        @NotBlank(message = "카테고리 명을 입력해주세요.")
        @Size(max = 10, message = "카테고리 명은 최대 10자까지 입력할 수 있습니다.")
        String value) {
    public StoreCategory toEntity() {
        return StoreCategory.of(key, value);
    }
}
