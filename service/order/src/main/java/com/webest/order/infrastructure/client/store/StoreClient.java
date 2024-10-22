package com.webest.order.infrastructure.client.store;

import com.webest.order.infrastructure.client.store.dto.ProductResponse;
import com.webest.order.infrastructure.configuration.StoreFeignClientConfig;
import com.webest.order.infrastructure.client.store.dto.StoreResponse;
import com.webest.web.response.CommonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "store-service",
        url = "http://127.0.0.1:19093",
        configuration = StoreFeignClientConfig.class,
        fallbackFactory = StoreFallbackFactory.class
)
public interface StoreClient {

    @GetMapping("/api/v1/stores/{id}")
    CommonResponse<StoreResponse> getStoreById(@PathVariable("id") Long id);

    // 상품 상점별 조회
    @GetMapping("/api/v1/products/stores/{storeId}")
    CommonResponse<Page<ProductResponse>> getProductsByStore(@PathVariable("storeId") Long storeId, @PageableDefault() Pageable pageable);

    // 상품 단건 조회
    @GetMapping("/api/v1/products/{id}")
    CommonResponse<ProductResponse> getProductById(@PathVariable("id") Long id);

}
