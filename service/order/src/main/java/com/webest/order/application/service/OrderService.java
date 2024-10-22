package com.webest.order.application.service;

import com.webest.order.application.dtos.OrderDto;
import com.webest.order.application.dtos.OrderProductDto;
import com.webest.order.application.dtos.OrderSearchDto;
import com.webest.order.application.dtos.OrderUpdateDto;
import com.webest.order.domain.events.OrderPaymentCompletedEvent;
import com.webest.order.domain.exception.ErrorCode;
import com.webest.order.domain.exception.OrderException;
import com.webest.order.domain.model.Order;
import com.webest.order.domain.model.OrderProduct;
import com.webest.order.domain.model.OrderStatus;
import com.webest.order.domain.repository.order.OrderRepository;
import com.webest.order.domain.service.*;
import com.webest.order.domain.validator.OrderProductValidator;
import com.webest.order.infrastructure.client.coupon.dto.CouponByUserResponseDto;
import com.webest.order.infrastructure.client.store.dto.ProductResponse;
import com.webest.order.infrastructure.client.user.UserClient;
import com.webest.order.presentation.response.OrderResponse;
import com.webest.order.infrastructure.client.store.dto.StoreResponse;
import com.webest.order.infrastructure.client.user.dto.UserResponse;
import com.webest.web.common.UserRole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final OrderEventService orderEventService;

    private final OrderProductService orderProductService;

    private final UserService userService;

    private final StoreService storeService;

    private final OrderProductValidator orderProductValidator;

    private final OrderProductDomainService orderProductDomainService;

    private final CouponService couponService;

    private final OrderCouponDomainService orderCouponDomainService;

    /**
     * 주문 생성
     * @param request 주문 생성에 필요한 정보를 담은 OrderDto
     * @return 저장된 주문 데이터를 담은 OrderResponse
     */
    @Transactional
    public OrderResponse createOrder(String userId, UserRole userRole, OrderDto request) {


        // 유저 정보 가져오기 (유저 주소값)
        UserResponse userResponse = userService.getUser(userId);

        // 상점 정보 가져오기 (상점 주소값)
        StoreResponse storeResponse = storeService.getStore(request.storeId());

        // 상점아이디로 존재하는 상품 검색
        Page<ProductResponse> productResponse = storeService.getProductsByStore(request.storeId(), Pageable.unpaged());

        // 상점에 포함된 상품만 요청가능하게 validate
        orderProductValidator.validateProductExistence(productResponse, request);

        // 주문 상품 create
        List<OrderProduct> orderProducts =
                orderProductDomainService.createOrderProduct(request);

        // coupon 적용
        List<CouponByUserResponseDto> couponByUserResponseDtoList =
                couponService.findCouponByUserId(userId, userRole, false);

        // 사용된 쿠폰 validation
        orderCouponDomainService.validateCouponUsage(
                request.couponId(), couponByUserResponseDtoList
        );

        Order order = Order.create(
                request.storeId(),
                request.paymentId(),
                request.couponId(),
                userId,
                request.orderStatus(),
                request.isRequest(),
                request.requestsToStore(),
                request.requestsToRider(),
                userResponse.addressCode(),
                userResponse.detailAddress(),
                storeResponse.storeAddress().addressCode(),
                storeResponse.storeAddress().detailAddress(),
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
        orderEventService.publishOrderCreatedEvent(order.createdEvent());

        return OrderResponse.of(order);
    }


    /**
     * 주문 완료
     */
    @Transactional
    public void paymentCompleteOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(ErrorCode.ORDER_NOT_FOUND));

        OrderPaymentCompletedEvent paymentCompletedEvent = order.paymentCompletedEvent();

        orderRepository.save(order);

        orderEventService.publishOrderPaymentCompletedEvent(paymentCompletedEvent);

    }

    /**
     * 주문 단건 조회
     * @param orderId 조회할 주문의 고유 ID (Long)
     * @return 조회된 주문 데이터를 담은 OrderResponse
     */
    @Transactional
    public OrderResponse getOrder(Long orderId) {
        return OrderResponse.of(orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(ErrorCode.ORDER_NOT_FOUND)));
    }

    /**
     * 주문 전체 조회
     * @return 모든 주문을 담고 있는 OrderResponse 리스트
     */
    @Transactional
    public List<OrderResponse> getAllOrders() {

        List<Order> orders = orderRepository.findAll();

        return OrderResponse.of(orders);
    }

    /**
     * 주문 수정
     * @param orderId 주문을 업데이트하기 위한 ID
     * @param request 주문 업데이트에 필요한 데이터
     * @return 수정된 주문 데이터를 담은 OrderResponse
     */
    @Transactional
    public OrderResponse updateOrder(String userId, UserRole userRole, Long orderId, OrderUpdateDto request) {


        return orderRepository.findById(orderId).map(order -> {
            order.update(
                    request.storeId(),
                    request.paymentId(),
                    request.couponId(),
                    userId,
                    request.orderStatus(),
                    request.isRequest(),
                    request.requestsToStore(),
                    request.requestsToRider(),
                    request.storeAddressCode(),
                    request.storeDetailAddress(),
                    request.arrivalAddressCode(),
                    request.arrivalDetailAddress(),
                    request.totalQuantity(),
                    request.totalProductPrice(),
                    request.couponAppliedAmount(),
                    request.deliveryTipAmount(),
                    request.totalPaymentPrice()
            );
            orderEventService.publishOrderUpdatedEvent(order.updatedEvent());
            return OrderResponse.of(order);
        }).orElseThrow(() -> new OrderException(ErrorCode.ORDER_NOT_FOUND));
    }

    /**
     * 주문 삭제
     * @param orderId 삭제할 주문의 ID
     */
    @Transactional
    public void deleteOrder(String userId, UserRole userRole, Long orderId) {
        orderRepository.findById(orderId).map(order -> {

            order.delete();

            orderEventService.publishOrderDeletedEvent(order.deletedEvent());

            return OrderResponse.of(order);
        }).orElseThrow(() -> new OrderException(ErrorCode.ORDER_NOT_FOUND));
    }


    /**
     * 주문 검색
     * @param userId 유저 아이디 값
     */
    @Transactional
    public Page<OrderResponse> searchOrders(String userId, UserRole userRole, OrderSearchDto request, PageRequest pageRequest) {

        return orderRepository.searchOrders(request, pageRequest)
                .map(OrderResponse::of);
    }

    /**
     * 주문 확인 (주문 상태값이 CONFIRMING_ORDER -> PREPARING 변경)
     * @param orderId 주문 아이디 값
     */
    @Transactional
    public void preparing(String userId, UserRole userRole, Long orderId) {
        orderRepository.findById(orderId).map(order -> {

            order.preparing();

            if(order.getOrderStatus() == OrderStatus.CONFIRMING_ORDER) {
                throw new OrderException(ErrorCode.ALREADY_CANCELLED_ORDER);
            }

            orderEventService.publishOrderPreparingEvent(order.preparingEvent());

            return OrderResponse.of(order);
        }).orElseThrow(() -> new OrderException(ErrorCode.ORDER_NOT_FOUND));
    }

    /**
     * 주문 요청(배달)
     * @param orderId 배달 요청할 주문의 ID
     */
    @Transactional
    public OrderResponse requestOrder(String userId, UserRole userRole, Long orderId) {

       return orderRepository.findById(orderId).map(order -> {

           // 주문 상태가 PREPARING이 아닐때 배달요청 불가
           if (order.getOrderStatus() != OrderStatus.PREPARING) {
               throw new OrderException(ErrorCode.INVALID_ORDER_STATUS);
           }

           order.requestOrder();

           orderEventService.publishOrderRequestEvent(order.requestedEvent());

           return OrderResponse.of(order);
        }).orElseThrow(() -> new OrderException(ErrorCode.ORDER_NOT_FOUND));

    }

    /**
     * 주문 완료(배달완료 되었을때 이벤트를 받아서 처리)
     * @param orderId 주문 완료 주문의 ID
     */
    @Transactional
    public OrderResponse completeOrder(Long orderId) {

        return orderRepository.findById(orderId).map(order -> {


            // payment 기능 완료 될 시 paymentStatus에 따라 주문취소 혹은 주문완료 구현

            order.complete();

            orderEventService.publishOrderCompletedEvent(order.completedEvent());

            return OrderResponse.of(order);
        }).orElseThrow(() -> new OrderException(ErrorCode.ORDER_NOT_FOUND));

    }

    /**
     * 주문 취소
     * @param orderId 주문 아이디 값
     */
    @Transactional
    public OrderResponse cancelOrder(String userId, UserRole userRole, Long orderId) {

        return orderRepository.findById(orderId).map(order -> {

            order.cancel();

            orderEventService.publishOrderCanceledEvent(order.canceledEvent());

            return OrderResponse.of(order);
        }).orElseThrow(() -> new OrderException(ErrorCode.ORDER_NOT_FOUND));

    }

    @Transactional
    public OrderResponse rollbackOrder(Long orderId) {

        return orderRepository.findById(orderId).map(order -> {

            // 롤백 되면 주문상태가 준비중으로 바뀜
            order.preparing();

            // 주문이 롤백 되었을 때 이벤트 발행
            orderEventService.publishOrderRollbackEvent(order.rollbackEvent());

            return OrderResponse.of(order);
        }).orElseThrow(() -> new OrderException(ErrorCode.ORDER_NOT_FOUND));

    }



}

