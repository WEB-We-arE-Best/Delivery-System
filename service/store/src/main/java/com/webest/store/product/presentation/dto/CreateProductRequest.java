package com.webest.store.product.presentation.dto;

import jakarta.validation.constraints.*;

public record CreateProductRequest(
        @NotNull(message = "상점 ID는 필수 입력 항목입니다.")
        Long storeId,
        @NotBlank(message = "상품 이름을 입력해주세요.")
        @Size(max = 20, message = "상품 이름은 최대 20자까지 입력할 수 있습니다.")
        String name,
        @NotNull(message = "가격은 필수 입력 항목입니다.")
        @DecimalMin(value = "0.0", inclusive = false, message = "상품 가격은 0보다 커야 합니다.")
        Double price,
        @Size(max = 50, message = "상품 설명은 최대 50자까지 입력할 수 있습니다.")
        String description
) {
}
