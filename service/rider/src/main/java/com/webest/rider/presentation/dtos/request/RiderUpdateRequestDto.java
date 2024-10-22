package com.webest.rider.presentation.dtos.request;

import com.webest.rider.domain.model.RiderTransportation;
import com.webest.web.validator.EnumValidation;
import jakarta.validation.constraints.NotNull;

public record RiderUpdateRequestDto(
    @EnumValidation(target = RiderTransportation.class)
    @NotNull(message = "배송수단 정보를 입력해주세요")
    String transportation
) {

}
