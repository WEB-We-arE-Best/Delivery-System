package com.webest.coupon.presentation;

import static com.webest.coupon.common.value.CouponStaticValue.COUPON_PARTITION_NUMBER;
import static com.webest.coupon.common.value.CouponStaticValue.KAFKA_COUPON_ISSUE_GROUP_ID;
import static com.webest.coupon.common.value.CouponStaticValue.KAFKA_COUPON_ISSUE_TOPIC_ID;

import com.webest.coupon.application.CouponUserService;
import com.webest.coupon.common.parser.KafkaEventSerializer;
import com.webest.coupon.domain.service.CouponRedisService;
import com.webest.coupon.presentation.dtos.request.CouponKafkaIssueDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CouponKafkaEndpoint {

    private final CouponUserService couponUserService;
    private final CouponRedisService couponRedisService;

    @KafkaListener(
        groupId = KAFKA_COUPON_ISSUE_GROUP_ID,
        topics = KAFKA_COUPON_ISSUE_TOPIC_ID,
        concurrency = COUPON_PARTITION_NUMBER
    )
    public void issueCouponSequence(String message, @Headers MessageHeaders messageHeaders) {
        CouponKafkaIssueDto data = KafkaEventSerializer.deserialize(message,
            CouponKafkaIssueDto.class);

        Integer partitionId = (Integer) messageHeaders.get(KafkaHeaders.RECEIVED_PARTITION);

//        log.info("큐에서 처리 시작 : {}  {}", data.toString(), partitionId);
        // 쿠폰 발급을 진행해준다.
        try {
            couponUserService.issueCouponToUser(data.couponId(), data.userId());
        } finally {
            // 쿠폰 처리 진행을 완료한 후에 큐에서 처리 완료 처리를 위해서 큐에서 제거 한다.
            couponRedisService.popCouponFromQueue(data.couponId(), data.userId());
        }

        try {
            //TODO: 테스트 용 현재 Production 예정이 없으므로 남겨둔다. 추후 배포시 삭제
            // Thread.sleep(100);
//            log.info("처리 완료 : {}", data);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
