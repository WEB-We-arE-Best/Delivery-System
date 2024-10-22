package com.webest.store.store.infrastructure.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserResponse(
        String userId,
        String userName,
        String email,
        Long addressCode,
        String detailAddress,
        double latitude,
        double longitude
) {
}
