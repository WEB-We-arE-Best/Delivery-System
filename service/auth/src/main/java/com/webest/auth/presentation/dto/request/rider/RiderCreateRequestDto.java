package com.webest.auth.presentation.dto.request.rider;

import com.webest.auth.domain.model.RiderTransportation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RiderCreateRequestDto(
    @Size(min = 1, max = 15)
    @NotBlank(message = "라이더 아이디를 입력해주세요")
    String userId,
    @Size(min = 5, max = 15)
    @NotBlank(message = "라이더 비밀번호를 입력해주세요")
    String password,
    @NotBlank(message = "라이더 휴대전화를 입력해주세요")
    String phoneNumber,
    @NotNull(message = "배송수단 정보를 입력해주세요")
    RiderTransportation transportation
) {

}
