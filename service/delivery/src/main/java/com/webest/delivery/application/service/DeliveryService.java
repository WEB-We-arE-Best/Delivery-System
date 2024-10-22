package com.webest.delivery.application.service;

import com.webest.delivery.application.dtos.DeliveryDto;
import com.webest.delivery.domain.exception.DeliveryException;
import com.webest.delivery.domain.exception.ErrorCode;
import com.webest.delivery.domain.model.Delivery;
import com.webest.delivery.domain.model.DeliveryStatus;
import com.webest.delivery.domain.repository.DeliveryRepository;
import com.webest.delivery.presentation.response.DeliveryResponse;
import com.webest.web.common.UserRole;
import com.webest.web.exception.ApplicationException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryEventService deliveryEventService;

    // controller 에서 직접 들어온 delivery 처리
    @Transactional
    public DeliveryResponse createDelivery(String userId, UserRole userRole, DeliveryDto request) {

        Delivery delivery = Delivery.create(
                request.orderId(),
                request.riderId(),
                request.requestsToRider(),
                request.deliveryStatus(),
                request.storeAddressCode(),
                request.storeDetailAddress(),
                request.arrivalAddressCode(),
                request.arrivalDetailAddress(),
                request.deliveryFeeAmount()
        );

        // 배달 저장
        deliveryRepository.save(delivery);

        // 배달 이벤트 작성해야함
        deliveryEventService.publishDeliveryCreatedEvent(delivery.createdEvent());


        return DeliveryResponse.of(delivery);
    }

    // orderEvent 로 들어온 배달 생성
    @Transactional
    public DeliveryResponse createDelivery(DeliveryDto request) {

        Delivery delivery = Delivery.create(
                request.orderId(),
                request.riderId(),
                request.requestsToRider(),
                request.deliveryStatus(),
                request.storeAddressCode(),
                request.storeDetailAddress(),
                request.arrivalAddressCode(),
                request.arrivalDetailAddress(),
                request.deliveryFeeAmount()
        );

        // 배달 저장
        deliveryRepository.save(delivery);

        // 배달 이벤트 작성해야함
        deliveryEventService.publishDeliveryCreatedEvent(delivery.createdEvent());

        return DeliveryResponse.of(delivery);
    }

    @Transactional
    public DeliveryResponse getDelivery(String userId, UserRole userRole, Long deliveryId) {

        return DeliveryResponse.of(deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryException(ErrorCode.DELIVERY_NOT_FOUND)));
    }

    @Transactional
    public List<DeliveryResponse> getAllDeliveries(String userId, UserRole userRole) {

        return DeliveryResponse.of(deliveryRepository.findAll());
    }


    @Transactional
    public DeliveryResponse updateDelivery(String userId, UserRole userRole, Long deliveryId, DeliveryDto request) {
            
        return deliveryRepository.findById(deliveryId).map(delivery -> {
            delivery.update(
                    request.orderId(),
                    request.riderId(),
                    request.requestsToRider(),
                    request.deliveryStatus(),
                    request.storeAddressCode(),
                    request.storeDetailAddress(),
                    request.arrivalAddressCode(),
                    request.arrivalDetailAddress(),
                    request.deliveryFeeAmount()
            );

            deliveryEventService.publishDeliveryUpdatedEvent(delivery.updatedEvent());

            return DeliveryResponse.of(delivery);
        }).orElseThrow(() -> new DeliveryException(ErrorCode.DELIVERY_NOT_FOUND));
    }

    @Transactional
    public void deleteDelivery(String userId, UserRole userRole, Long deliveryId) {
        deliveryRepository.findById(deliveryId).map(delivery -> {
            
            delivery.delete();

            deliveryEventService.publishDeliveryCanceledEvent(delivery.canceledEvent());
            return DeliveryResponse.of(delivery);
        }).orElseThrow(() -> new DeliveryException(ErrorCode.DELIVERY_NOT_FOUND));
    }


    @Transactional
    public Page<DeliveryResponse> searchDeliveries(String userId, UserRole userRole, DeliveryDto request, PageRequest pageRequest) {

        return deliveryRepository.searchDelivery(request, pageRequest)
                .map(DeliveryResponse::of);
    }

    @Transactional
    public DeliveryResponse dispatchDelivery(String userId, UserRole userRole, Long deliveryId) {
        return deliveryRepository.findById(deliveryId).map(delivery -> {

            delivery.dispatch(userId);

            deliveryEventService.publishDeliveryDispatchedEvent(delivery.dispatchedEvent());

            return DeliveryResponse.of(delivery);
        }).orElseThrow(() -> new DeliveryException(ErrorCode.DELIVERY_NOT_FOUND));
    }

    @Transactional
    public DeliveryResponse departureDelivery(String userId, UserRole userRole, Long deliveryId) {
        return deliveryRepository.findById(deliveryId).map(delivery -> {

            delivery.departure();

            deliveryEventService.publishDeliveryDepartedEvent(delivery.departedEvent());

            return DeliveryResponse.of(delivery);
        }).orElseThrow(() -> new DeliveryException(ErrorCode.DELIVERY_NOT_FOUND));
    }

    @Transactional
    public DeliveryResponse completeDelivery(String userId, UserRole userRole, Long deliveryId) {
        return deliveryRepository.findById(deliveryId).map(delivery -> {

            delivery.complete();

            deliveryEventService.publishDeliveryCompletedEvent(delivery.completedEvent());

            return DeliveryResponse.of(delivery);
        }).orElseThrow(() -> new DeliveryException(ErrorCode.DELIVERY_NOT_FOUND));
    }

    // 주문 취소 -> 배달 취소 orderId 값을 event 로 받아 배달도 취소
    @Transactional
    public void cancelDelivery(Long orderId) {
            deliveryRepository.findByOrderId(orderId).map(delivery -> {

            delivery.cancel();

            // 배송 취소 이벤트 추가 해야함
            deliveryEventService.publishDeliveryCanceledEvent(delivery.canceledEvent());

            return DeliveryResponse.of(delivery);
        }).orElseThrow(() -> new DeliveryException(ErrorCode.DELIVERY_NOT_FOUND));

    }

    // controller 에서 직접 들어온 delivery 취소 처리
    @Transactional
    public DeliveryResponse cancelDelivery(String userid, UserRole userRole, Long orderId) {
        return deliveryRepository.findByOrderId(orderId).map(delivery -> {

            if(delivery.getDeliveryStatus() != DeliveryStatus.REQUEST) {
                throw new DeliveryException(ErrorCode.INVALID_ORDER_STATUS);
            }

            delivery.cancel();

            // 배송 취소 이벤트 추가 해야함
            deliveryEventService.publishDeliveryCanceledEvent(delivery.canceledEvent());

            return DeliveryResponse.of(delivery);
        }).orElseThrow(() -> new DeliveryException(ErrorCode.DELIVERY_NOT_FOUND));
    }


    /**
     * 30분 내로 배차가 되지 않은 배달 롤백 처리
     */
    @Transactional
    @Scheduled(fixedRate = 60000) // 1분마다 스케줄 실행
    public void rollbackUndispatchedDeliveries() {

        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusMinutes(30);

        // 배차되지 않은 배달 찾기 (배달 상태가 REQUEST이고, 30분이 경과한 경우)
        List<Delivery> undispatchedDeliveries = deliveryRepository.findByDeliveryStatusAndCreatedAtBefore(
                DeliveryStatus.REQUEST, thirtyMinutesAgo);

        undispatchedDeliveries.forEach(delivery -> {

            // 배달 롤백
            delivery.cancel();

            // 롤백 이벤트 발행
            deliveryEventService.publishDeliveryRollbackEvent(delivery.rollbackEvent());

            // 배달 상태 저장
            deliveryRepository.save(delivery);
        });
    }


}
