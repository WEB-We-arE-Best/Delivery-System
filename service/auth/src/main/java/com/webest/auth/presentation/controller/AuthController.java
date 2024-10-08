package com.webest.auth.presentation.controller;

import com.webest.auth.application.AuthService;
import com.webest.auth.infrastructure.email.MailSendService;
import com.webest.auth.infrastructure.email.dto.EmailCheckDto;
import com.webest.auth.infrastructure.email.dto.EmailRequest;
import com.webest.auth.presentation.dto.request.RiderCreateRequestDto;
import com.webest.auth.presentation.dto.request.UserJoinRequest;
import com.webest.auth.presentation.dto.response.JoinResponse;
import com.webest.web.response.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final MailSendService mailService;

    // 회원 가입
    @PostMapping("/signUp")
    public CommonResponse<JoinResponse> createUser(@RequestBody @Valid UserJoinRequest userJoinRequest) {
        JoinResponse response = authService.create(userJoinRequest);

        return CommonResponse.success(response);
    }

    // 라이더 회원 가입
    @PostMapping("/riderSignUp")
    public CommonResponse<Long> createRider(@RequestBody @Valid RiderCreateRequestDto requestDto) {
        Long riderId = authService.createRider(requestDto);

        return CommonResponse.success(riderId);
    }

    @PostMapping ("/mailSend")
    public String mailSend(@RequestBody @Valid EmailRequest emailDto){
        System.out.println("이메일 인증 요청이 들어옴");
        System.out.println("이메일 인증 이메일 :"+emailDto.email());
        mailService.joinEmail(emailDto.email());
        return "인증번호가 전송되었습니다";
    }

    @PostMapping("/mailCheck")
    public String AuthCheck(@RequestBody @Valid EmailCheckDto dto){
        Boolean Checked=mailService.CheckAuthNum(dto.email(),dto.authNum());
        if(Checked){
            return "인증이 완료되었습니다.";
        }
        else{
            throw new NullPointerException("인증 정보가 잘 못 되었습니다 다시 인증해주세요");
        }
    }
}
