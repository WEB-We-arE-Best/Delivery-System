package com.webest.store.category.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(
        @NotNull(message = "수정할 카테고리의 ID를 입력해주세요.")
        Long id,
        @NotBlank(message = "카테고리 명을 입력해주세요.")
        @Size(max = 10, message = "카테고리 명은 최대 10자까지 입력할 수 있습니다.")
        String value) {
}
