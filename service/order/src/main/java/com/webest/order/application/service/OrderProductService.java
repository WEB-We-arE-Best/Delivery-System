package com.webest.order.application.service;

import com.webest.order.application.dtos.OrderProductDto;
import com.webest.order.domain.model.OrderProduct;
import com.webest.order.domain.repository.orderproduct.OrderProductRepository;
import com.webest.order.domain.service.StoreService;
import com.webest.order.presentation.response.OrderProductResponse;
import com.webest.web.exception.ApplicationException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderProductService {

    private final OrderProductRepository orderProductRepository;


    // OrderProductDto List로 받아 여러건의 주문 상품을 저장
    @Transactional
    public List<OrderProduct> createOrderProduct(List<OrderProductDto> requestList) {

        // 각 OrderProductDto를 OrderProduct로 변환 후 저장
        List<OrderProduct> orderProducts = requestList.stream()
                .map(request -> OrderProduct.create(
                        request.productId(),
                        request.quantity(),
                        request.price()
                ))
                .collect(Collectors.toList());

        // 리스트로 저장
        return orderProductRepository.saveAll(orderProducts);
    }



    @Transactional
    public OrderProductResponse updateOrderProduct(Long orderProductId, OrderProductDto request) {

        return orderProductRepository.findById(orderProductId).map(orderProduct -> {
            orderProduct.update(
                    request.productId(),
                    request.quantity(),
                    request.price(),
                    request.totalPrice());
            return OrderProductResponse.of(orderProduct);
        }).orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "주문"));
    }




}
