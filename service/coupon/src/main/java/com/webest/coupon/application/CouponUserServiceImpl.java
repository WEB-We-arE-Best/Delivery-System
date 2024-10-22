package com.webest.coupon.application;

import static com.webest.coupon.common.value.CouponRedisStatus.CLOSE;
import static com.webest.coupon.common.value.CouponRedisStatus.OUT_OF_STOCK;
import static com.webest.coupon.common.value.CouponStaticValue.KAFKA_COUPON_ISSUE_TOPIC_ID;

import com.webest.coupon.application.client.UserClient;
import com.webest.coupon.common.aop.RedissonLock;
import com.webest.coupon.common.exception.CouponErrorCode;
import com.webest.coupon.common.exception.CouponException;
import com.webest.coupon.common.parser.KafkaEventSerializer;
import com.webest.coupon.domain.dtos.CouponByUserDto;
import com.webest.coupon.domain.model.Coupon;
import com.webest.coupon.domain.model.CouponUser;
import com.webest.coupon.domain.repository.CouponQueryRepository;
import com.webest.coupon.domain.repository.CouponRepository;
import com.webest.coupon.domain.service.CouponDomainService;
import com.webest.coupon.domain.service.CouponRedisService;
import com.webest.coupon.mapper.CouponMapper;
import com.webest.coupon.presentation.dtos.request.CouponKafkaIssueDto;
import com.webest.coupon.presentation.dtos.response.CouponByUserResponseDto;
import com.webest.coupon.presentation.dtos.response.CouponUserResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CouponUserServiceImpl implements CouponUserService {

    // repository
    private final CouponRepository couponRepository;
    private final CouponQueryRepository couponQueryRepository;

    // domain
    private final CouponRedisService couponRedisService;
    private final CouponDomainService couponDomainService;

    // mapper
    private final CouponMapper couponMapper;

    // client
    private final UserClient userClient;

    // kafka
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public List<CouponByUserResponseDto> findCouponListByUser(String userId, Boolean used) {

        List<CouponByUserDto> couponListByUserId =
            couponQueryRepository.findCouponListByUserId(userId, used);

        return couponMapper.couponByUserDtoToResponseDto(couponListByUserId);
    }

    @Override
    public CouponUserResponseDto findUserCouponById(String userId, Long userCouponId) {
        Coupon couponInfo = findCouponByUserCouponId(userCouponId);

        if (couponInfo.getCouponUserList().isEmpty()) {
            throw new CouponException(CouponErrorCode.NOT_OWNED_COUPON);
        }

        return CouponUserResponseDto.from(couponInfo.getCouponUserList().get(0));
    }

    @Override
    public Long checkCurrentOffsetInWaiting(Long couponId, String userId) {
        Coupon coupon = couponByIdWithLockAndCheck(couponId);
        // 현재 쿠폰이 열려있는지 확인한다.
        checkCouponIsOpen(coupon);

        // 현재 offset을 반환한다.
        return couponRedisService.checkCurrentOffset(couponId, userId);
    }

    @RedissonLock(value = "#couponId")
    @Transactional
    public boolean issueCouponToUser(Long couponId, String userId) {
        // user가 존재하는 지 확인한다.
        userClient.findUserById(userId);

        Coupon coupon = couponByIdWithLockAndCheck(couponId);

        // 쿠폰 발급이 가능한지 확인한다.
        couponDomainService.checkIssueCouponCondition(coupon);

        // issue coupon
        LocalDateTime expiredTime = coupon.issueCoupon();

        CouponUser couponUser = CouponUser.from(userId, coupon, expiredTime);
        coupon.addUserToCoupon(couponUser);

        return true;
    }

    @Override
    public boolean issueCouponWithQueue(Long couponId, String userId) {
        // user가 존재하는 지 확인한다.
        userClient.findUserById(userId);

        Coupon coupon = couponByIdWithLockAndCheck(couponId);
        // 현재 쿠폰이 열려있는지 확인한다.
        checkCouponIsOpen(coupon);

        // 발급 대기 중인 유저인지 확인 하고
        // 이미 대기 중인 경우에는 큐로 보내지 않는다.
        Long rank = couponRedisService.checkCurrentOffset(couponId, userId);
        if (rank != null) {
            return true;
        }

        CompletableFuture<SendResult<String, String>> send = kafkaTemplate.send(
            KAFKA_COUPON_ISSUE_TOPIC_ID,
//            "coupon-issue", // 특정 파티션에 보내지 않기 위해서 키 미적용
            KafkaEventSerializer.serialize(new CouponKafkaIssueDto(couponId, userId)));

        // 현재 진행 상황을 알기 위해 RedisQueue에 넣어준다.
        couponRedisService.addCouponToQueue(couponId, userId);

        return false;
    }

    @Transactional
    @Override
    public boolean useCouponByUser(Long userCouponId, Long couponId, String userId) {
        // user가 존재하는 지 확인한다.
        userClient.findUserById(userId);

        Coupon coupon = findCouponByUserCouponId(userCouponId);

        List<CouponUser> couponUserList = coupon.getCouponUserList();
        // 쿠폰을 보유하고 있는지 확인
        if (couponUserList.isEmpty()) {
            throw new CouponException(CouponErrorCode.NOT_OWNED_COUPON);
        }

        CouponUser couponUser = couponUserList.get(0);

        // 쿠폰을 보유하고 있는지 확인
        if (!Objects.equals(couponUser.getUserId(), userId)
            || !Objects.equals(couponUser.getCoupon().getCouponId(), couponId)) {
            throw new CouponException(CouponErrorCode.NOT_OWNED_COUPON);
        }

        // 쿠폰 사용
        couponUser.useCoupon();

        return true;
    }

    /* ==========================================================================================
            private method
        ==========================================================================================*/

    /**
     * coupon 기본 조회
     * TODO: 쿠폰 정보를 Redis로 넣어보자
     */
    private Coupon couponByIdWithLockAndCheck(Long id) {
        return couponRepository.findById(id).orElseThrow(() ->
            new CouponException(CouponErrorCode.COUPON_NOT_FOUND)
        );
    }

    /**
     * 쿠폰과 유저 아이디의 기준으로 쿠폰 조회
     */
    private Coupon couponByIdWithCouponUser(Long couponId, String userId) {
        return couponRepository.findCouponByCouponIdAndUserId(couponId, userId)
            .orElseThrow(() ->
                new CouponException(CouponErrorCode.NOT_OWNED_COUPON)
            );
    }

    /**
     * userCoupon의 아이디로 조회한다.
     */
    private Coupon findCouponByUserCouponId(Long userCouponId) {
        return couponRepository.findCouponByCouponUserId(userCouponId)
            .orElseThrow(() ->
                new CouponException(CouponErrorCode.NOT_OWNED_COUPON)
            );
    }

    /**
     * 현재 쿠폰이 발급이 가능한지 확인한다.
     */
    private void checkCouponIsOpen(Coupon coupon) {
        Integer currentStatus = couponRedisService.checkCouponStatus(coupon.getCouponId());

        if (currentStatus == null) {
            couponDomainService.checkIssueCouponCondition(coupon);
        }

        if (currentStatus != null && currentStatus.equals(
            OUT_OF_STOCK.getValue())) {
            throw new CouponException(CouponErrorCode.COUPON_OUT_OF_STOCK);
        } else if (currentStatus != null && currentStatus.equals(
            CLOSE.getValue())) {
            throw new CouponException(CouponErrorCode.COUPON_CLOSED);
        }

        // 열려있으면 에러 핸들링을 하지 않는다.
        return;
    }
}
