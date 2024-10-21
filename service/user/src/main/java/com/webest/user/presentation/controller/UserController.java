package com.webest.user.presentation.controller;

import com.webest.user.application.service.UserService;
import com.webest.user.domain.model.vo.ShoppingCartDto;
import com.webest.user.presentation.dto.request.UserJoinRequest;
import com.webest.user.presentation.dto.request.UserUpdateRequest;
import com.webest.user.presentation.dto.response.OrderProductResponse;
import com.webest.user.presentation.dto.response.UserResponse;
import com.webest.web.response.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    // 유저 생성
    @PostMapping("/signUp")
    public UserResponse createUser(@RequestBody @Valid UserJoinRequest request) {
        UserResponse response = userService.create(request);

        return response;
    }

    // 유저 전체 출력
    @GetMapping()
    public CommonResponse<?> getUsers(
            @RequestHeader("X-role") String role
    ){
        if(role.equals("admin")){
            return CommonResponse.error(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
        }
        return CommonResponse.success(userService.getUserByAll());
    }

    // 현재 로그인한 유저 데이터 출력
    @GetMapping("/myPage")
    public CommonResponse<UserResponse> getUser(
            @RequestHeader("X-UserId") String userId
    ){
        return CommonResponse.success(userService.getUser(userId));
    }

    // 유저 업데이트
    @PatchMapping("/myPage/update")
    public CommonResponse<UserResponse> updateUser(
            @RequestHeader("X-UserId") String userId,
            @RequestBody UserUpdateRequest request
    ){
        return CommonResponse.success(userService.update(userId,request));
    }

    // 유저 삭제
    @DeleteMapping("/delete/{userId}")
    public CommonResponse<String> deleteUser(@PathVariable ("userId") Long userId,
                                             @RequestHeader("X-UserId") String xUserId){
        userService.delete(userId,xUserId);
        return CommonResponse.success(userId + "의 유저가 삭제되었습니다");
    }

    // 장바구니 조회
    @GetMapping("/cart")
    public CommonResponse<OrderProductResponse> getUserCart(
            @RequestHeader("X-UserId") String userId){
        return CommonResponse.success(userService.getCart(userId));
    }
}
