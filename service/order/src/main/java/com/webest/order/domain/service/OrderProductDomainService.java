package com.webest.order.domain.service;

import com.webest.order.application.dtos.OrderDto;
import com.webest.order.application.dtos.OrderProductDto;
import com.webest.order.domain.exception.ErrorCode;
import com.webest.order.domain.exception.OrderException;
import com.webest.order.domain.model.OrderProduct;
import com.webest.order.infrastructure.client.store.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderProductDomainService {

    private final StoreService storeService;


    // 상품 아이디의 정보로 orderProduct 생성
    public List<OrderProduct> createOrderProduct(OrderDto request) {

        // Step 1: 주문된 상품의 정보(ProductResponse) 가져오기
        List<ProductResponse> productResponses = getProductInfo(request);

        // Step 2: 주문 상품(OrderProduct) 생성
        List<OrderProduct> orderProducts = request.orderProductDtos().stream()
                .map(orderProductDto -> {
                    // Step 3: ProductResponse에서 해당 상품의 정보를 가져옴
                    ProductResponse productResponse = productResponses.stream()
                            .filter(p -> p.id().equals(orderProductDto.productId()))
                            .findFirst()
                            .orElseThrow(() -> new OrderException(ErrorCode.PRODUCT_NOT_FOUND));

                    // Step 4: OrderProduct 객체 생성
                    return OrderProduct.create(
                            productResponse.id(),
                            orderProductDto.quantity(),
                            productResponse.price() // ProductResponse에서 가져온 상품 가격 사용
                    );

                })
                .collect(Collectors.toList());

        return orderProducts;
    }

    public List<ProductResponse> getProductInfo(OrderDto request) {
        // Step 1: 주문 상품 ID 리스트 추출
        List<Long> orderProductIds = request.orderProductDtos().stream()
                .map(OrderProductDto::productId) // OrderProductDto에서 productId 추출
                .toList();

        // Step 2: 각 productId에 대해 storeService에서 product 정보를 가져옴
        List<ProductResponse> productResponses = orderProductIds.stream()
                .map(storeService::getProductById) // storeService.getProductById() 호출
                .collect(Collectors.toList()); // 결과를 리스트로 수집

        return productResponses; // 상품 정보를 반환
    }

}
