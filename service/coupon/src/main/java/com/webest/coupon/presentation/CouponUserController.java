package com.webest.coupon.presentation;


import com.webest.coupon.application.CouponUserService;
import com.webest.coupon.common.exception.CouponErrorCode;
import com.webest.coupon.common.exception.CouponException;
import com.webest.coupon.presentation.dtos.request.CouponUsedRequestDto;
import com.webest.coupon.presentation.dtos.response.CouponByUserResponseDto;
import com.webest.coupon.presentation.dtos.response.CouponIssueResponseDto;
import com.webest.coupon.presentation.dtos.response.CouponOffsetResponseDto;
import com.webest.coupon.presentation.dtos.response.CouponUserResponseDto;
import com.webest.web.common.CommonStaticVariable;
import com.webest.web.common.UserRole;
import com.webest.web.response.CommonResponse;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/coupons")
public class CouponUserController {

    private final CouponUserService couponUserService;

    /**
     * 유저 소유한 쿠폰 목록 조회 (자기 자신만 조회 가능)
     */
    @GetMapping("/users")
    public CommonResponse<List<CouponByUserResponseDto>> findCouponByUserId(
        @RequestHeader(name = CommonStaticVariable.X_USER_ID) String xUserId,
        @RequestHeader(name = CommonStaticVariable.X_USER_ROLE) UserRole userRole,
        @RequestParam(value = "used", required = false) Boolean used
    ) {
        List<CouponByUserResponseDto> couponListByUser = couponUserService.findCouponListByUser(
            xUserId, used);

        return CommonResponse.success(couponListByUser);
    }

    /**
     * 마스터가 유저 선택해서 보유 쿠폰 조회 (마스터만 가능)
     */
    @GetMapping("/users/{userId}")
    public CommonResponse<List<CouponByUserResponseDto>> findCouponByUserIdWithAdmin(
        @PathVariable("userId") String userId,
        @RequestHeader(name = CommonStaticVariable.X_USER_ROLE) UserRole userRole,
        @RequestParam(value = "used", required = false) Boolean used
    ) {
        if (!userRole.equals(UserRole.MASTER)) {
            throw new CouponException(CouponErrorCode.NOT_ADMIN);
        }

        List<CouponByUserResponseDto> couponListByUser = couponUserService.findCouponListByUser(
            userId, used);

        return CommonResponse.success(couponListByUser);
    }

    /**
     * 유저 쿠폰 단일 조회
     */
    @GetMapping("/single/{id}")
    public CommonResponse<CouponUserResponseDto> findUserCouponById(
        @PathVariable("id") Long userCouponId,
        @RequestHeader(name = CommonStaticVariable.X_USER_ID) String userId
    ) {

        CouponUserResponseDto couponUserInfo = couponUserService.findUserCouponById(
            userId, userCouponId);

        if (!couponUserInfo.userId().equals(userId)) {
            throw new CouponException(CouponErrorCode.NOT_OWNED_COUPON);
        }

        return CommonResponse.success(couponUserInfo);
    }

    /**
     * 대기중인 유저의 현재 위치 조회
     */
    @GetMapping("/users/position")
    public CommonResponse<CouponOffsetResponseDto> getCurrentOffsetInWaiting(
        @RequestHeader(name = CommonStaticVariable.X_USER_ID) String xUserId,
        @RequestHeader(name = CommonStaticVariable.X_USER_ROLE) UserRole userRole,
        @RequestParam("couponId") Long couponId,
        @RequestParam("userId") String userId
    ) {
        checkRequestUserIsSame(userRole, userId, xUserId);

        Long offset = couponUserService.checkCurrentOffsetInWaiting(couponId, userId);

        if (offset == null) {
            throw new CouponException(CouponErrorCode.NOT_FOUND_IN_POSITION);
        }

        return CommonResponse.success(new CouponOffsetResponseDto(userId, couponId, offset));
    }

    /**
     * 쿠폰 발급
     */
    @PostMapping("/{couponId}/users/{userId}")
    public CommonResponse<Boolean> issueCoupon(
        @RequestHeader(name = CommonStaticVariable.X_USER_ID) String xUserId,
        @RequestHeader(name = CommonStaticVariable.X_USER_ROLE) UserRole userRole,
        @PathVariable("couponId") Long couponId,
        @PathVariable("userId") String userId
    ) {
        checkRequestUserIsSame(userRole, userId, xUserId);

        boolean success = couponUserService.issueCouponToUser(couponId, userId);

        return CommonResponse.success(success);
    }

    /**
     * 비동기로 쿠폰 발급
     */
    @PostMapping("/{couponId}/users/{userId}/queue")
    public CommonResponse<CouponIssueResponseDto> queueCouponIssuance(
        @RequestHeader(name = CommonStaticVariable.X_USER_ID) String xUserId,
        @RequestHeader(name = CommonStaticVariable.X_USER_ROLE) UserRole userRole,
        @PathVariable("couponId") Long couponId,
        @PathVariable("userId") String userId
    ) {
        checkRequestUserIsSame(userRole, userId, xUserId);

        boolean alreadyWaiting = couponUserService.issueCouponWithQueue(couponId, userId);
        CouponIssueResponseDto responseDto;

        // 이미 쿠폰에 존재하는 지 확인 한다.
        if (alreadyWaiting) {
            responseDto = new CouponIssueResponseDto("이미 발급 대기 중인 상태입니다.");
        } else {
            responseDto = new CouponIssueResponseDto("쿠폰 신청 완료하였습니다.");
        }

        return CommonResponse.success(responseDto);
    }

    /**
     * 쿠폰 사용
     */
    @PatchMapping("/used")
    public CommonResponse<Boolean> useCoupon(
        @RequestHeader(name = CommonStaticVariable.X_USER_ID) String xUserId,
        @RequestHeader(name = CommonStaticVariable.X_USER_ROLE) UserRole userRole,
        @Validated @RequestBody CouponUsedRequestDto requestDto
    ) {
        checkRequestUserIsSame(userRole, requestDto.userId(), xUserId);

        boolean success = couponUserService.useCouponByUser(
            requestDto.userCouponId(),
            requestDto.couponId(),
            requestDto.userId());

        return CommonResponse.success(success);
    }

    /* ========================================================================
     Private method
     ======================================================================== */

    /**
     * 요청한 유저와 수행되는 유저가 같은 지 확인한다.
     */
    private void checkRequestUserIsSame(UserRole role, String userId, String xUserId) {
        // 마스터면 모두 가능
        if (role.equals(UserRole.MASTER)) {
            return;
        }

        // 요청 유저와 수행 유저가 다름.
        if (!Objects.equals(userId, xUserId)) {
            throw new CouponException(CouponErrorCode.NOT_SELF_USER);
        }
    }

}
