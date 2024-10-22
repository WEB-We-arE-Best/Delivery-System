package com.webest.rider.application;

import com.webest.app.address.csv.ReadAddressCsv;
import com.webest.rider.common.exception.RiderErrorCode;
import com.webest.rider.common.exception.RiderException;
import com.webest.rider.domain.model.Rider;
import com.webest.rider.domain.model.RiderSearchCondition;
import com.webest.rider.domain.model.RiderTransportation;
import com.webest.rider.domain.model.vo.PhoneNumber;
import com.webest.rider.domain.repository.RiderQueryRepository;
import com.webest.rider.domain.repository.RiderRepository;
import com.webest.rider.presentation.dtos.request.RiderCreateRequestDto;
import com.webest.rider.presentation.dtos.request.RiderRegisterAddressRequestDto;
import com.webest.rider.presentation.dtos.request.RiderUpdateRequestDto;
import com.webest.rider.presentation.dtos.response.RiderResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RiderServiceImpl implements RiderService {

    private final RiderQueryRepository riderQueryRepository;
    private final RiderRepository riderRepository;
    private final ReadAddressCsv readAddressCsv;

    //비밀번호
    private final PasswordEncoder passwordEncoder;

    /**
     * 라이더 생성
     */
    @Transactional
    @Override
    public String createRider(RiderCreateRequestDto requestDto) {

        // userId 중복인지 확인
        checkDupUserId(requestDto.userId());

        // 휴대전화 중복 확인
        checkDupPhoneNumber(requestDto.phoneNumber());

        // 라이더 생성
        Rider rider = Rider.createRider(requestDto.userId(),
            passwordEncoder.encode(requestDto.password()),
            requestDto.phoneNumber(), RiderTransportation.valueOf(requestDto.transportation()));
        Rider riderInfo = riderRepository.save(rider);

        return riderInfo.getUserId();
    }

    /**
     * 라이더 단일 조회
     */
    @Override
    public RiderResponseDto findRiderById(Long riderId) {
        Rider riderByIdAndCheck = findRiderByIdAndCheck(riderId);
        return RiderResponseDto.from(riderByIdAndCheck);
    }

    @Override
    public RiderResponseDto findByUserId(String userId) {
        Rider riderByIdAndCheck = findRiderByUserIdAndCheck(userId);
        return RiderResponseDto.from(riderByIdAndCheck);
    }

    /**
     * 라이더 목록 조회
     */
    @Override
    public Page<RiderResponseDto> findRiderListByCondition(Pageable pageable,
        RiderSearchCondition condition) {

        Page<Rider> riderListByQuery = riderQueryRepository.findRiderListByQuery(pageable,
            condition);

        return riderListByQuery.map(RiderResponseDto::from);
    }

    /**
     * 라이더 로그인 확인
     */
    @Override
    public RiderResponseDto checkSignUp(String userId, String password) {
        Rider rider = findRiderByUserIdAndCheck(userId);

        // 안맞으면 에러
        if (!passwordEncoder.matches(password, rider.getPassword())) {
            throw new RiderException(RiderErrorCode.INVALID_PASSWORD);
        }

        return RiderResponseDto.from(rider);
    }

    /**
     * 라이더에 주소 등록
     */
    @Transactional
    @Override
    public RiderResponseDto registerAddressToRider(String riderId,
        RiderRegisterAddressRequestDto requestDto) {

        Rider rider = findRiderByUserIdAndCheck(riderId);

        // Address code가 존재하는 지 확인
        requestDto.addressCodeList().forEach(
            (code) -> {
                if (readAddressCsv.findAddressByCode(code) == null) {
                    throw new RiderException(RiderErrorCode.ADDRESS_NOT_FOUND);
                }
            }
        );

        rider.registerAddress(requestDto.addressCodeList());

        return RiderResponseDto.from(rider);
    }

    /**
     * 라이더 정보 업데이트
     */
    @Transactional
    @Override
    public RiderResponseDto updateRiderById(String userId, RiderUpdateRequestDto requestDto) {

        Rider rider = findRiderByUserIdAndCheck(userId);
        rider.updateRiderInfo(RiderTransportation.valueOf(requestDto.transportation()));

        return RiderResponseDto.from(rider);
    }

    /**
     * 라이더 정보 삭제
     */
    @Transactional
    @Override
    public Long deleteRiderById(Long id) {

        Rider rider = findRiderByIdAndCheck(id);
        rider.deleteRider();

        return rider.getRiderId();
    }


    /* ==========================================================================================
        private method
     ==========================================================================================*/

    /**
     * 라이더 ID 있는지 조회 후 확인
     */
    private Rider findRiderByIdAndCheck(Long riderId) {
        return riderRepository.findById(riderId)
            .orElseThrow(
                () -> new RiderException(RiderErrorCode.RIDER_NOT_FOUND));
    }

    /**
     * 라이더 userId가 있는지 확인
     */
    private Rider findRiderByUserIdAndCheck(String userId) {
        return riderRepository.findByUserId(userId)
            .orElseThrow(
                () -> new RiderException(RiderErrorCode.RIDER_NOT_FOUND));
    }

    private void checkDupUserId(String userId) {
        riderRepository.findByUserId(userId).ifPresent(
            rider -> {
                throw new RiderException(RiderErrorCode.EXIST_USER_ID);
            }
        );
    }

    /**
     * 이미 존재하는 휴대전화 번호인지 확인한다.
     */
    private void checkDupPhoneNumber(String phoneNumber) {
        // 휴대폰 중복 확인
        boolean riderPhoneCheck = riderRepository.existsByPhone(new PhoneNumber(phoneNumber));

        if (riderPhoneCheck) {
            throw new RiderException(
                RiderErrorCode.EXIST_PHONE_NUMBER
            );
        }
    }
}
