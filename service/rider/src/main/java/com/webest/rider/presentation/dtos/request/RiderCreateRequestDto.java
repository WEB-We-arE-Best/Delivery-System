package com.webest.rider.presentation.dtos.request;

import com.webest.rider.domain.model.RiderTransportation;
import com.webest.web.validator.EnumValidation;
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

    @EnumValidation(target = RiderTransportation.class)
    @NotNull(message = "배송수단 정보를 입력해주세요")
    String transportation
) {

}
