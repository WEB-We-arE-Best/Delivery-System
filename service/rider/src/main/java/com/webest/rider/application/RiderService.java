package com.webest.rider.application;

import com.webest.rider.domain.model.RiderSearchCondition;
import com.webest.rider.presentation.dtos.request.RiderCreateRequestDto;
import com.webest.rider.presentation.dtos.request.RiderRegisterAddressRequestDto;
import com.webest.rider.presentation.dtos.request.RiderUpdateRequestDto;
import com.webest.rider.presentation.dtos.response.RiderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RiderService {

    /**
     * 라이더 생성
     */
    public String createRider(RiderCreateRequestDto requestDto);

    /**
     * 라이더에 주소 Code 리스트 등록
     */
    public RiderResponseDto registerAddressToRider(String userId,
        RiderRegisterAddressRequestDto requestDto);

    /**
     * 라이더 단일 조회
     */
    public RiderResponseDto findByUserId(String userId);

    /**
     * 라이더 단일 조회
     */
    public RiderResponseDto findRiderById(Long userId);

    /**
     * 라이더 목록 조회
     */
    public Page<RiderResponseDto> findRiderListByCondition(Pageable pageable,
        RiderSearchCondition condition);

    /**
     * 라이더 로그인
     */
    public RiderResponseDto checkSignUp(String userId, String password);

    /**
     * 라이더 정보 업데이트
     */
    public RiderResponseDto updateRiderById(String userId, RiderUpdateRequestDto requestDto);

    /**
     * 라이더 삭제
     */
    public Long deleteRiderById(Long id);

}
