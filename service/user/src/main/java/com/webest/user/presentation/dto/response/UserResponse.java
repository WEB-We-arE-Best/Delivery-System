package com.webest.user.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserResponse(
        String userId,
        String userName,
        String email,
        Long addressCode
) {
}