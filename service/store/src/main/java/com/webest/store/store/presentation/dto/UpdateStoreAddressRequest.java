package com.webest.store.store.presentation.dto;

import jakarta.validation.constraints.*;

public record UpdateStoreAddressRequest(
        @NotNull(message = "상점 ID를 입력해주세요.")
        @Positive(message = "상점 ID는 양수여야 합니다.")
        Long storeId,
        @NotBlank(message = "시(도)를 입력해주세요.")
        String city,
        @NotBlank(message = "구(군)를 입력해주세요.")
        String street,
        @NotBlank(message = "동(읍/면)을 입력해주세요.")
        String district,
        @NotBlank(message = "상세 주소를 입력해주세요.")
        String detailAddress
) {}