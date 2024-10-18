package com.webest.rider.presentation;


import com.webest.rider.application.RiderService;
import com.webest.rider.presentation.dtos.request.RiderCreateRequestDto;
import com.webest.rider.presentation.dtos.request.RiderLoginRequestDto;
import com.webest.rider.presentation.dtos.response.RiderResponseDto;
import com.webest.web.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/internal/api/v1/riders")
public class RiderEndPoint {

    private final RiderService riderService;

    @PostMapping("/sign-in")
    public CommonResponse<RiderResponseDto> authenticateRider(
        @RequestBody RiderLoginRequestDto requestDto
    ) {

        RiderResponseDto riderResponseDto = riderService.checkSignUp(requestDto.userId(),
            requestDto.password());

        return CommonResponse.success(riderResponseDto);
    }


}
