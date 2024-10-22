package com.webest.auth.infrastructure.core;

import com.webest.auth.infrastructure.core.dto.RiderResponseDto;
import com.webest.auth.presentation.dto.request.rider.RiderAuthRequestDto;
import com.webest.auth.presentation.dto.request.rider.RiderCreateRequestDto;
import com.webest.web.response.CommonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "rider-service")
public interface RiderClient {

    @PostMapping("/api/v1/riders")
    CommonResponse<String> createRider(@Validated @RequestBody RiderCreateRequestDto requestDto);

    @PostMapping("/internal/api/v1/riders/sign-in")
    CommonResponse<RiderResponseDto> authRider(
        @Validated @RequestBody RiderAuthRequestDto requestDto);
}