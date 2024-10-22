package com.webest.order.domain.validator;

import com.webest.order.application.dtos.OrderDto;
import com.webest.order.application.dtos.OrderProductDto;
import com.webest.order.domain.exception.ErrorCode;
import com.webest.order.domain.exception.OrderException;
import com.webest.order.domain.service.StoreService;
import com.webest.order.infrastructure.client.store.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderProductValidator {

    private final StoreService storeService;

    public void validateProductExistence(Page<ProductResponse> productResponse, OrderDto request) {

        // 상품 ID 리스트 추출
        List<Long> availableProductIds = productResponse.stream()
                .map(ProductResponse::id)
                .toList();

        // 주문 상품 리스트 확인 (주문 상품의 ID가 상점에 있는 상품 ID와 일치하는지 확인)
        boolean hasInvalidProduct = request.orderProductDtos().stream()
                .map(OrderProductDto::productId)
                .anyMatch(orderProductId -> !availableProductIds.contains(orderProductId));

        // 만약 hasInvalidProduct false 일때 PRODUCT_NOT_FOUND 에러코드 발생
        if (hasInvalidProduct) {
            throw new OrderException(ErrorCode.PRODUCT_NOT_FOUND);
        }

    }



}
