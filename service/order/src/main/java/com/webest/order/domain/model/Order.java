package com.webest.order.domain.model;


import com.webest.app.jpa.BaseEntity;
import com.webest.order.application.dtos.OrderProductDto;
import com.webest.order.domain.events.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SQLDelete(sql = "UPDATE p_store SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Table(name = "p_order")
public class Order extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long storeId;

    private Long paymentId;

    private Long couponId;

    private String userId;

    @Enumerated(EnumType.STRING)  // Enum을 문자열로 DB에 저장
    private OrderStatus orderStatus;

    private Boolean isRequest; // 주문 -> 배달 요청여부

    private String requestsToStore; // 가게에게 요청사항

    private String requestsToRider; // 배송원에게 요청사항

    private Long storeAddressCode; // 가게 주소코드
    
    private String storeDetailAddress; // 가게 상세주소

    private Long arrivalAddressCode; // 도착 주소코드

    private String arrivalDetailAddress; // 도착 상세주소

    private Integer totalQuantity; // 총 수량

    private Double totalProductPrice; // 총 상품 가격

    private Double couponAppliedAmount; // 쿠폰 적용 가격

    private Double deliveryTipAmount; // 배달팁 가격

    private Double totalPaymentPrice; // 총 결제 가격
    
    private Double deliveryFeeAmount; // 배달료

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderProduct> orderProducts;

    public static Order create(Long storeId,
                               Long paymentId,
                               Long couponId,
                               String userId,
                               OrderStatus orderStatus,
                               Boolean isRequest,
                               String requestsToStore,
                               String requestsToRider,
                               Long arrivalAddressCode,
                               String arrivalDetailAddress,
                               Long storeAddressCode,
                               String storeDetailAddress,
                               Integer totalQuantity,
                               Double totalProductPrice,
                               Double couponAppliedAmount,
                               Double deliveryTipAmount,
                               Double totalPaymentPrice,
                               List<OrderProduct> orderProducts)
    {

        Order order = new Order();
        order.storeId = storeId;
        order.paymentId = paymentId;
        order.couponId = couponId;
        order.userId = userId;
        order.orderStatus = orderStatus;
        order.isRequest = isRequest;
        order.requestsToStore = requestsToStore;
        order.requestsToRider = requestsToRider;
        order.arrivalAddressCode = arrivalAddressCode;
        order.arrivalDetailAddress = arrivalDetailAddress;
        order.storeAddressCode = storeAddressCode;
        order.storeDetailAddress = storeDetailAddress;
        order.totalQuantity = calculateTotalQuantity(orderProducts);
        order.totalProductPrice = calculateTotalProductPrice(orderProducts);
        order.couponAppliedAmount = couponAppliedAmount;
        order.deliveryTipAmount = deliveryTipAmount;
        order.totalPaymentPrice = calculateTotalPaymentPrice(order.totalProductPrice, couponAppliedAmount, deliveryTipAmount);
        order.orderProducts = orderProducts;
        orderProducts.forEach(orderProduct -> orderProduct.setOrder(order));

        return order;
    }

    public void update(Long storeId,
                       Long paymentId,
                       Long couponId,
                       String userId,
                       OrderStatus orderStatus,
                       Boolean isRequest,
                       String requestsToStore,
                       String requestsToRider,
                       Long storeAddressCode,
                       String storeDetailAddress,
                       Long arrivalAddressCode,
                       String arrivalDetailAddress,
                       Integer totalQuantity,
                       Double totalProductPrice,
                       Double couponAppliedAmount,
                       Double deliveryTipAmount,
                       Double totalPaymentPrice) {
        this.storeId = storeId;
        this.paymentId = paymentId;
        this.couponId = couponId;
        this.userId = userId;
        this.orderStatus = orderStatus;
        this.isRequest = isRequest;
        this.requestsToStore = requestsToStore;
        this.requestsToRider = requestsToRider;
        this.storeAddressCode = storeAddressCode;
        this.storeDetailAddress = storeDetailAddress;
        this.arrivalAddressCode = arrivalAddressCode;
        this.arrivalDetailAddress = arrivalDetailAddress;
        this.totalQuantity = calculateTotalQuantity(orderProducts);
        this.totalProductPrice = calculateTotalProductPrice(orderProducts);
        this.couponAppliedAmount = couponAppliedAmount;
        this.deliveryTipAmount = deliveryTipAmount;
        this.totalPaymentPrice = calculateTotalPaymentPrice(calculateTotalProductPrice(orderProducts), couponAppliedAmount, deliveryTipAmount);
    }

    // 주문 삭제
    public void delete() {
        this.isDeleted = true;
    }

    public void cancel() {
        this.orderStatus = OrderStatus.CANCEL;
    }

    // 주문 확인중에서 음식 준비중으로 변경 (CONFIRMING_ORDER -> PREPARING)
    public void preparing() {
        this.orderStatus = OrderStatus.PREPARING;
    }

    // 주문 완료
    public void complete() {
        this.orderStatus = OrderStatus.COMPLETE;
    }


    // 배달로 요청
    public void requestOrder() {
        this.isRequest = true;
    }


    public OrderCreatedEvent createdEvent() {
        return new OrderCreatedEvent(
                this.id,
                this.storeId,
                this.paymentId,
                this.couponId,
                this.userId,
                this.orderStatus,
                this.isRequest,
                this.requestsToStore,
                this.requestsToRider,
                this.storeAddressCode,
                this.storeDetailAddress,
                this.arrivalAddressCode,
                this.arrivalDetailAddress,
                this.totalQuantity,
                this.totalProductPrice,
                this.couponAppliedAmount,
                this.deliveryTipAmount,
                this.totalPaymentPrice,
                this.orderProducts
                        .stream()
                        .map(OrderProduct::toDto)
                        .collect(Collectors.toList()));

    }

    public OrderUpdatedEvent updatedEvent() {
        return new OrderUpdatedEvent(
                this.id,
                this.storeId,
                this.paymentId,
                this.couponId,
                this.userId,
                this.orderStatus,
                this.isRequest,
                this.requestsToStore,
                this.requestsToRider,
                this.storeAddressCode,
                this.storeDetailAddress,
                this.arrivalAddressCode,
                this.arrivalDetailAddress,
                this.totalQuantity,
                this.totalProductPrice,
                this.couponAppliedAmount,
                this.deliveryTipAmount,
                this.totalPaymentPrice);
    }

    public OrderPaymentCompletedEvent paymentCompletedEvent() {
        this.orderStatus = OrderStatus.CONFIRMING_ORDER;
        return new OrderPaymentCompletedEvent(
                this.id,
                this.storeId,
                this.paymentId,
                this.couponId,
                this.userId,
                this.orderStatus,
                this.isRequest,
                this.requestsToStore,
                this.requestsToRider,
                this.storeAddressCode,
                this.storeDetailAddress,
                this.arrivalAddressCode,
                this.arrivalDetailAddress,
                this.totalQuantity,
                this.totalProductPrice,
                this.couponAppliedAmount,
                this.deliveryTipAmount,
                this.totalPaymentPrice);
        }



    public OrderCanceledEvent canceledEvent() {
        this.orderStatus = OrderStatus.CANCEL;
        return new OrderCanceledEvent(
                this.id);
    }

    public OrderPreparingEvent preparingEvent() {
        return new OrderPreparingEvent(
                this.id,
                this.storeId,
                this.paymentId,
                this.couponId,
                this.userId,
                this.orderStatus,
                this.isRequest,
                this.requestsToStore,
                this.requestsToRider,
                this.storeAddressCode,
                this.storeDetailAddress,
                this.arrivalAddressCode,
                this.arrivalDetailAddress,
                this.totalQuantity,
                this.totalProductPrice,
                this.couponAppliedAmount,
                this.deliveryTipAmount,
                this.totalPaymentPrice);
    }

    public OrderRequestedEvent requestedEvent() {
        this.orderStatus = OrderStatus.DELIVERING;
        return new OrderRequestedEvent(
                this.id,
                this.storeId,
                this.paymentId,
                this.couponId,
                this.userId,
                this.orderStatus,
                this.isRequest,
                this.requestsToStore,
                this.requestsToRider,
                this.storeAddressCode,
                this.storeDetailAddress,
                this.arrivalAddressCode,
                this.arrivalDetailAddress,
                this.totalQuantity,
                this.totalProductPrice,
                this.couponAppliedAmount,
                this.deliveryTipAmount,
                this.totalPaymentPrice);
    }


    public OrderCompletedEvent completedEvent() {
        this.orderStatus = OrderStatus.COMPLETE;
        return new OrderCompletedEvent(
                this.id,
                this.storeId,
                this.paymentId,
                this.couponId,
                this.userId,
                this.orderStatus,
                this.isRequest,
                this.requestsToStore,
                this.requestsToRider,
                this.storeAddressCode,
                this.storeDetailAddress,
                this.arrivalAddressCode,
                this.arrivalDetailAddress,
                this.totalQuantity,
                this.totalProductPrice,
                this.couponAppliedAmount,
                this.deliveryTipAmount,
                this.totalPaymentPrice);
    }

    public OrderDeletedEvent deletedEvent() {
        this.isDeleted = true;
        return new OrderDeletedEvent(
                this.id,
                this.storeId,
                this.paymentId,
                this.couponId,
                this.userId,
                this.orderStatus,
                this.isRequest,
                this.requestsToStore,
                this.requestsToRider,
                this.storeAddressCode,
                this.storeDetailAddress,
                this.arrivalAddressCode,
                this.arrivalDetailAddress,
                this.totalQuantity,
                this.totalProductPrice,
                this.couponAppliedAmount,
                this.deliveryTipAmount,
                this.totalPaymentPrice);
    }

    public OrderRollbackEvent rollbackEvent() {
        this.orderStatus = OrderStatus.PREPARING;
        return new OrderRollbackEvent(
                this.id,
                this.storeId,
                this.paymentId,
                this.couponId,
                this.userId,
                this.orderStatus,
                this.isRequest,
                this.requestsToStore,
                this.requestsToRider,
                this.storeAddressCode,
                this.storeDetailAddress,
                this.arrivalAddressCode,
                this.arrivalDetailAddress,
                this.totalQuantity,
                this.totalProductPrice,
                this.couponAppliedAmount,
                this.deliveryTipAmount,
                this.totalPaymentPrice);
    }

    // 각 OrderProduct의 quantity 값을 더해 totalQuantity 계산
    private static Integer calculateTotalQuantity(List<OrderProduct> orderProducts) {
        return orderProducts.stream()
                .mapToInt(OrderProduct::getQuantity)
                .sum();
    }

    // 각 OrderProduct의 totalPrice 값을 더해 totalProductPrice 계산
    private static Double calculateTotalProductPrice(List<OrderProduct> orderProducts) {
        return orderProducts.stream()
                .mapToDouble(OrderProduct::getTotalPrice)
                .sum();
    }

    // totalProductPrice +
    private static Double calculateTotalPaymentPrice(Double totalProductPrice, Double couponAppliedAmount, Double deliveryTipAmount) {
        return totalProductPrice + deliveryTipAmount - couponAppliedAmount;
    }

}
