package com.webest.order.infrastructure.client.store;

import com.webest.order.infrastructure.client.store.dto.ProductResponse;
import com.webest.order.infrastructure.client.store.dto.StoreResponse;
import com.webest.order.infrastructure.client.user.dto.UserResponse;
import com.webest.web.response.CommonResponse;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Slf4j
public class StoreFallback implements StoreClient {

    private final Throwable cause;

    public StoreFallback(Throwable cause) {
        this.cause = cause;
    }


    @Override
    public CommonResponse<StoreResponse> getStoreById(Long id) {
        if (cause instanceof FeignException.NotFound) {
            log.error("Not found error");
        }
        log.error("Failed to get user {}", id);
        return new CommonResponse<>(404, "상점 데이터를 가져오지 못했습니다.", null);
    }

    @Override
    public CommonResponse<Page<ProductResponse>> getProductsByStore(Long storeId, Pageable pageable) {
        if (cause instanceof FeignException.NotFound) {
            log.error("Not found error");
        }
        log.error("Failed to get product {}", storeId);
        return new CommonResponse<>(404, "상품 데이터를 가져오지 못했습니다.", null);
    }

    @Override
    public CommonResponse<ProductResponse> getProductById(Long id) {
        if (cause instanceof FeignException.NotFound) {
            log.error("Not found error");
        }
        log.error("Failed to get product {}", id);
        return new CommonResponse<>(404, "상품 데이터를 가져오지 못했습니다.", null);
    }


}
