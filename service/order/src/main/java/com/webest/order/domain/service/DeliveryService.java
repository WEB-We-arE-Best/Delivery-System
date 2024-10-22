package com.webest.order.domain.service;


import com.webest.order.infrastructure.client.delivery.DeliveryClient;
import com.webest.order.infrastructure.client.delivery.dto.DeliveryCreateRequest;
import com.webest.order.infrastructure.client.delivery.dto.DeliveryResponse;
import com.webest.web.common.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryClient deliveryClient;

    public void createDelivery(String userId, UserRole userRole, DeliveryCreateRequest deliveryCreateRequest) {
        deliveryClient.createDelivery(userId, userRole, deliveryCreateRequest);
    }

}
