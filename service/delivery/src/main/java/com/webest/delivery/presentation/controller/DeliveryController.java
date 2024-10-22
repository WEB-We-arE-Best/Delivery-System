package com.webest.delivery.presentation.controller;

import com.webest.delivery.application.service.DeliveryService;
import com.webest.delivery.domain.model.DeliveryStatus;
import com.webest.delivery.presentation.reqeust.DeliveryCreateRequest;
import com.webest.delivery.presentation.reqeust.DeliverySearchRequest;
import com.webest.delivery.presentation.reqeust.DeliveryUpdateRequest;
import com.webest.delivery.presentation.response.DeliveryResponse;
import com.webest.web.common.CommonStaticVariable;
import com.webest.web.common.UserRole;
import com.webest.web.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;


    @PostMapping
    public CommonResponse<DeliveryResponse> createDelivery(@RequestHeader(name = CommonStaticVariable.X_USER_ID) String userId,
                                                           @RequestHeader(name = CommonStaticVariable.X_USER_ROLE) UserRole userRole,
                                                           @RequestBody DeliveryCreateRequest createRequest) {


        return CommonResponse.success(deliveryService.createDelivery(userId, userRole, createRequest.toDto()));
    }

    @GetMapping("/{deliveryId}")
    public CommonResponse<DeliveryResponse> getDelivery(@RequestHeader(name = CommonStaticVariable.X_USER_ID) String userId,
                                                        @RequestHeader(name = CommonStaticVariable.X_USER_ROLE) UserRole userRole,
                                                        @PathVariable(name = "deliveryId") Long deliveryId) {


        return CommonResponse.success(deliveryService.getDelivery(userId, userRole, deliveryId));
    }

    @GetMapping
    public CommonResponse<List<DeliveryResponse>> getAllDeliveries(@RequestHeader(name = CommonStaticVariable.X_USER_ID) String userId,
                                                                   @RequestHeader(name = CommonStaticVariable.X_USER_ROLE) UserRole userRole) {


        return CommonResponse.success(deliveryService.getAllDeliveries(userId, userRole));
    }

    @PatchMapping("/{deliveryId}")
    public CommonResponse<DeliveryResponse> updateDelivery(@RequestHeader(name = CommonStaticVariable.X_USER_ID) String userId,
                                                           @RequestHeader(name = CommonStaticVariable.X_USER_ROLE) UserRole userRole,
                                                           @PathVariable(name = "deliveryId") Long deliveryId,
                                                           @RequestBody DeliveryUpdateRequest updateRequest) {


        return CommonResponse.success(deliveryService.updateDelivery(userId, userRole, deliveryId, updateRequest.toDto()));
    }

    @DeleteMapping("/{deliveryId}")
    public void deleteDelivery(@RequestHeader(name = CommonStaticVariable.X_USER_ID) String userId,
                               @RequestHeader(name = CommonStaticVariable.X_USER_ROLE) UserRole userRole,
                               @PathVariable(name = "deliveryId") Long deliveryId) {

        deliveryService.deleteDelivery(userId, userRole, deliveryId);
    }

    @GetMapping("/search")
    public CommonResponse<?> searchDeliveries(@RequestHeader(name = CommonStaticVariable.X_USER_ID) String userId,
                                              @RequestHeader(name = CommonStaticVariable.X_USER_ROLE) UserRole userRole,
                                              @RequestParam(required = false) Long orderId,
                                              @RequestParam(required = false) String riderId,
                                              @RequestParam(required = false) String requestsToRider,
                                              @RequestParam(required = false) DeliveryStatus deliveryStatus,
                                              @RequestParam(required = false) Long storeAddressCode,
                                              @RequestParam(required = false) String storeDetailAddress,
                                              @RequestParam(required = false) Long arrivalAddressCode,
                                              @RequestParam(required = false) String arrivalDetailAddress,
                                              @RequestParam(required = false) Double deliveryFeeAmount,
                                              @RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "20") int size) {

        PageRequest pageRequest = PageRequest.of(page - 1, size);

        DeliverySearchRequest searchRequest = new DeliverySearchRequest(
                orderId,
                riderId,
                requestsToRider,
                deliveryStatus,
                storeAddressCode,
                storeDetailAddress,
                arrivalAddressCode,
                arrivalDetailAddress,
                deliveryFeeAmount
        );

        return CommonResponse.success(deliveryService.searchDeliveries(userId, userRole, searchRequest.toDto(), pageRequest));
    }



    // 배차
    @PostMapping("/{deliveryId}/dispatch")
    public CommonResponse<?> dispatchDelivery(@RequestHeader(name = CommonStaticVariable.X_USER_ID) String userId,
                               @RequestHeader(name = CommonStaticVariable.X_USER_ROLE) UserRole userRole,
                               @PathVariable(name = "deliveryId") Long deliveryId) {


        return CommonResponse.success(deliveryService.dispatchDelivery(userId, userRole, deliveryId));
    }

    // 출발
    @PostMapping("/{deliveryId}/departure")
    public CommonResponse<?> departureDelivery(@RequestHeader(name = CommonStaticVariable.X_USER_ID) String userId,
                                 @RequestHeader(name = CommonStaticVariable.X_USER_ROLE) UserRole userRole,
                                 @PathVariable(name = "deliveryId") Long deliveryId) {


       return CommonResponse.success(deliveryService.departureDelivery(userId, userRole, deliveryId));
    }

    // 완료
    @PostMapping("/{deliveryId}/complete")
    public CommonResponse<?> completeDelivery(@RequestHeader(name = CommonStaticVariable.X_USER_ID) String userId,
                                  @RequestHeader(name = CommonStaticVariable.X_USER_ROLE) UserRole userRole,
                                  @PathVariable(name = "deliveryId") Long deliveryId) {


        return CommonResponse.success(deliveryService.completeDelivery(userId, userRole, deliveryId));
    }

    // 취소
    @PostMapping("/{deliveryId}/cancel")
    public CommonResponse<?> cancelDelivery(@RequestHeader(name = CommonStaticVariable.X_USER_ID) String userId,
                                 @RequestHeader(name = CommonStaticVariable.X_USER_ROLE) UserRole userRole,
                                 @PathVariable(name = "deliveryId") Long deliveryId) {

        return CommonResponse.success(deliveryService.cancelDelivery(userId, userRole, deliveryId));
    }

    // 취소
    @PostMapping("/{deliveryId}/rollback")
    public CommonResponse<?> rollbackDelivery(@RequestHeader(name = CommonStaticVariable.X_USER_ID) String userId,
                                            @RequestHeader(name = CommonStaticVariable.X_USER_ROLE) UserRole userRole,
                                            @PathVariable(name = "deliveryId") Long deliveryId) {

        deliveryService.rollbackUndispatchedDeliveries();

        return CommonResponse.success(null);
    }







}
