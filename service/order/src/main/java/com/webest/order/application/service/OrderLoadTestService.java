package com.webest.order.application.service;

import com.webest.order.application.dtos.OrderDto;
import com.webest.order.domain.model.Order;
import com.webest.order.domain.model.OrderProduct;
import com.webest.order.domain.repository.order.OrderRepository;
import com.webest.order.domain.service.DeliveryService;
import com.webest.order.infrastructure.client.delivery.dto.DeliveryCreateRequest;
import com.webest.order.infrastructure.messaging.events.DeliveryStatus;
import com.webest.order.presentation.response.OrderResponse;
import com.webest.web.common.UserRole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderLoadTestService {

    private final OrderRepository orderRepository;

    private final OrderProductService orderProductService;

    private final DeliveryService deliveryService;

    private final OrderEventService orderEventService;


    // 카프카 이벤트 발생이 아니라 feignClient로 요청을 보내서 측정
    @Transactional
    public OrderResponse createOrderFeignClient(String userId, UserRole userRole, OrderDto request) {


        // 주문 상품 create
        List<OrderProduct> orderProducts =
                orderProductService.createOrderProduct(request.orderProductDtos());

        Order order = Order.create(
                request.storeId(),
                request.paymentId(),
                request.couponId(),
                userId,
                request.orderStatus(),
                request.isRequest(),
                request.requestsToStore(),
                request.requestsToRider(),
                null,
                null,
                null,
                null,
                request.totalQuantity(),
                request.totalProductPrice(),
                request.couponAppliedAmount(),
                request.deliveryTipAmount(),
                request.totalPaymentPrice(),
                orderProducts
        );

        // 주문 저장
        orderRepository.save(order);


        DeliveryCreateRequest deliveryCreateRequest = new DeliveryCreateRequest(
                order.getId(),
                order.getUserId(),
                order.getRequestsToRider(),
                DeliveryStatus.REQUEST,
                order.getStoreAddressCode(),
                order.getStoreDetailAddress(),
                order.getArrivalAddressCode(),
                order.getArrivalDetailAddress(),
                null
        );

        deliveryService.createDelivery(userId, userRole, deliveryCreateRequest);


        return OrderResponse.of(order);
    }

    @Transactional
    public OrderResponse createOrderKafka(String userId, UserRole userRole, OrderDto request) {


        // 주문 상품 create
        List<OrderProduct> orderProducts =
                orderProductService.createOrderProduct(request.orderProductDtos());

        Order order = Order.create(
                request.storeId(),
                request.paymentId(),
                request.couponId(),
                userId,
                request.orderStatus(),
                request.isRequest(),
                request.requestsToStore(),
                request.requestsToRider(),
                null,
                null,
                null,
                null,
                request.totalQuantity(),
                request.totalProductPrice(),
                request.couponAppliedAmount(),
                request.deliveryTipAmount(),
                request.totalPaymentPrice(),
                orderProducts
        );

        // 주문 저장
        orderRepository.save(order);


        // 주문 생성시 이벤트 발생
        orderEventService.publishOrderRequestEvent(order.requestedEvent());

        return OrderResponse.of(order);
    }




}
