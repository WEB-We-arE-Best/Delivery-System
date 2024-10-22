package com.webest.coupon.common.value;

public final class CouponStaticValue {

    /**
     * 쿠폰 카프카 상태 정보
     */
    public static final String KAFKA_COUPON_ISSUE_GROUP_ID = "coupon.issue.processor.group";
    public static final String KAFKA_COUPON_ISSUE_TOPIC_ID = "coupon.issuance";
    public static final String COUPON_PARTITION_NUMBER = "3";
    /**
     * 쿠폰 레디스 키
     */
    public static final String COUPON_REDIS_WAITING_KEY = "waiting-list:coupon";
    public static final String COUPON_REDIS_STATUS_KEY = "open-check:coupon:";
    /**
     * 로깅 이름
     */
    public final static String LOGGING_SERVICE_NAME = "coupon-service";
    /**
     * 쿠폰 비율 할인 최대 값
     */
    public static Integer PERCENTAGE_MAX_VALUE = 90;


    private CouponStaticValue() {
        // prevent init
        throw new UnsupportedOperationException("이 클래스는 인스턴스 생성을 지원하지 않습니다.");
    }
}
