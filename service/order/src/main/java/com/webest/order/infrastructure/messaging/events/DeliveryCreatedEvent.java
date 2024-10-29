package com.webest.order.infrastructure.messaging.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryCreatedEvent {

    private Long id;

    private Long orderId; // 주문 id

    private Long riderId; // 라이더 id

    private String requestsToRider;

    private DeliveryStatus deliveryStatus; // 배달 상태

    private Long storeAddressCode; // 가게 주소코드

    private String storeDetailAddress; // 가게 상세주소

    private Long arrivalAddressCode; // 도착 주소코드

    private String arrivalDetailAddress; // 도착 상세주소

    private Double deliveryFeeAmount; // 배달료
}
