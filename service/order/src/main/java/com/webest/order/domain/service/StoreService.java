package com.webest.order.domain.service;

import com.webest.order.infrastructure.client.store.StoreClient;
import com.webest.order.infrastructure.client.store.dto.ProductResponse;
import com.webest.order.infrastructure.client.store.dto.StoreResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreClient storeClient;

    public StoreResponse getStore(Long storeId) {
        return storeClient.getStoreById(storeId).getData();
    }

    // 상점별 상품 조회 (페이징 처리)
    public Page<ProductResponse> getProductsByStore(Long storeId, @PageableDefault(size = 10) Pageable pageable) {
        return storeClient.getProductsByStore(storeId, pageable).getData();
    }


    // 상품 단건 조회
    public ProductResponse getProductById(Long productId) {
        return storeClient.getProductById(productId).getData();
    }


}
