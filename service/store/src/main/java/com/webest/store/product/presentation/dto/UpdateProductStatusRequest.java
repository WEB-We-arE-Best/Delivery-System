package com.webest.store.product.presentation.dto;

import com.webest.store.product.domain.ProductStatus;
import jakarta.validation.constraints.*;

public record UpdateProductStatusRequest(
        @NotNull(message = "상태를 변경할 상품 ID를 입력해주세요.")
        Long productId,
        @NotNull(message = "변경할 상품 상태를 입력해주세요.")
        ProductStatus status) {
}
