package com.webest.store.product.presentation.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateProductDetailsRequest(
        @NotNull(message = "수정할 상품의 ID를 입력해주세요.")
        Long productId,
        @NotBlank(message = "상품 이름을 입력해주세요.")
        @Size(max = 20, message = "상품 이름은 최대 20자까지 입력할 수 있습니다.")
        String name,
        @DecimalMin(value = "0.0", inclusive = false, message = "상품 가격은 0보다 커야 합니다.")
        Double price,
        @Size(max = 50, message = "상품 설명은 최대 50자까지 입력할 수 있습니다.")
        String description) {
}
