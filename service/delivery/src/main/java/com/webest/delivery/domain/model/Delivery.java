package com.webest.delivery.domain.model;


import com.webest.app.jpa.BaseEntity;
import com.webest.delivery.domain.events.*;
import com.webest.delivery.infrastructure.messaging.events.OrderCanceledEvent;
import com.webest.delivery.presentation.reqeust.DeliveryUpdateRequest;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "p_delivery")
public class Delivery extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId; // 주문 id

    private String riderId; // 라이더 id

    private String requestsToRider;

    @Enumerated(EnumType.STRING)  // Enum을 문자열로 DB에 저장
    private DeliveryStatus deliveryStatus; // 배달 상태

    private Long storeAddressCode; // 가게 주소코드

    private String storeDetailAddress; // 가게 상세주소

    private Long arrivalAddressCode; // 도착 주소코드

    private String arrivalDetailAddress; // 도착 상세주소

    private Double deliveryFeeAmount; // 배달료

    public static Delivery create(Long orderId,
                                  String riderId,
                                  String requestsToRider,
                                  DeliveryStatus deliveryStatus,
                                  Long storeAddressCode,
                                  String storeDetailAddress,
                                  Long arrivalAddressCode,
                                  String arrivalDetailAddress,
                                  Double deliveryFeeAmount)
    {

        Delivery delivery = new Delivery();
        delivery.orderId = orderId;
        delivery.riderId = riderId;
        delivery.requestsToRider = requestsToRider;
        delivery.deliveryStatus = deliveryStatus;
        delivery.storeAddressCode = storeAddressCode;
        delivery.storeDetailAddress = storeDetailAddress;
        delivery.arrivalAddressCode = arrivalAddressCode;
        delivery.arrivalDetailAddress = arrivalDetailAddress;
        delivery.deliveryFeeAmount = deliveryFeeAmount;
        return delivery;
    }

    public void update(Long orderId,
                       String riderId,
                       String requestsToRider,
                       DeliveryStatus deliveryStatus,
                       Long storeAddressCode,
                       String storeDetailAddress,
                       Long arrivalAddressCode,
                       String arrivalDetailAddress,
                       Double deliveryFeeAmount) {
        this.orderId = orderId;
        this.riderId = riderId;
        this.requestsToRider = requestsToRider;
        this.deliveryStatus = deliveryStatus;
        this.storeAddressCode = storeAddressCode;
        this.storeDetailAddress = storeDetailAddress;
        this.arrivalAddressCode = arrivalAddressCode;
        this.arrivalDetailAddress = arrivalDetailAddress;
        this.deliveryFeeAmount = deliveryFeeAmount;
    }

    public void delete() {
        this.isDeleted = true;
    }

    // DeliveryStatus 배차 변경
    public void dispatch(String userId) {
        this.deliveryStatus = DeliveryStatus.DISPATCH;
        this.riderId = userId;
    }

    // DeliveryStatus 출발 변경
    public void departure() {
        this.deliveryStatus = DeliveryStatus.DEPARTURE;
    }
    // DeliveryStatus 완료 변경
    public void complete() {
        this.deliveryStatus = DeliveryStatus.COMPLETED;
    }

    // DeliveryStatus 취소 변경
    public void cancel() {
        this.deliveryStatus = DeliveryStatus.CANCELED;
    }

    public DeliveryCreatedEvent createdEvent() {
        return new DeliveryCreatedEvent(
                this.id,
                this.orderId,
                this.riderId,
                this.requestsToRider,
                this.deliveryStatus,
                this.storeAddressCode,
                this.storeDetailAddress,
                this.arrivalAddressCode,
                this.arrivalDetailAddress,
                this.deliveryFeeAmount);
    }

    public DeliveryUpdatedEvent updatedEvent() {
        return new DeliveryUpdatedEvent(
                this.id,
                this.orderId,
                this.riderId,
                this.requestsToRider,
                this.deliveryStatus,
                this.storeAddressCode,
                this.storeDetailAddress,
                this.arrivalAddressCode,
                this.arrivalDetailAddress,
                this.deliveryFeeAmount);
    }

    public DeliveryCanceledEvent canceledEvent() {
        this.deliveryStatus = DeliveryStatus.CANCELED;
        return new DeliveryCanceledEvent(
                this.id
        );
    }

    public DeliveryDispatchedEvent dispatchedEvent() {
        return new DeliveryDispatchedEvent(
                this.id,
                this.orderId,
                this.riderId,
                this.requestsToRider,
                this.deliveryStatus,
                this.storeAddressCode,
                this.storeDetailAddress,
                this.arrivalAddressCode,
                this.arrivalDetailAddress,
                this.deliveryFeeAmount);
    }

    public DeliveryDepartedEvent departedEvent() {
        return new DeliveryDepartedEvent(
                this.id,
                this.orderId,
                this.riderId,
                this.requestsToRider,
                this.deliveryStatus,
                this.storeAddressCode,
                this.storeDetailAddress,
                this.arrivalAddressCode,
                this.arrivalDetailAddress,
                this.deliveryFeeAmount);
    }

    public DeliveryCompletedEvent completedEvent() {
        return new DeliveryCompletedEvent(
                this.id,
                this.orderId,
                this.riderId,
                this.requestsToRider,
                this.deliveryStatus,
                this.storeAddressCode,
                this.storeDetailAddress,
                this.arrivalAddressCode,
                this.arrivalDetailAddress,
                this.deliveryFeeAmount);
    }

    public DeliveryRollbackEvent rollbackEvent() {
        return new DeliveryRollbackEvent(
                this.id,
                this.orderId,
                "요청한지 30분 지났음");
    }



}
