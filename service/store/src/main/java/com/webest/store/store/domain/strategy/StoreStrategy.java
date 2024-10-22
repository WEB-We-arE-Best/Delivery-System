package com.webest.store.store.domain.strategy;

import com.webest.store.store.presentation.dto.StoreResponse;
import com.webest.web.common.UserRole;

import java.util.List;

public interface StoreStrategy {
    // 배달 상점 조회
    List<StoreResponse> getDeliveryStores(String userId);

    // 배달 상점 검색
    List<StoreResponse> searchDeliveryStores(String userId, String keyword, Long categoryId);

    // 포장 상점 조회
    List<StoreResponse> getTakeOutStores(String userId, Double radius);

    boolean checkUserRole(UserRole role);
}
