package com.webest.auth.presentation.dto.request.rider;

import jakarta.validation.constraints.NotNull;

public record RiderAuthRequestDto(
    @NotNull
    String userId,
    @NotNull
    String password
) {

}
