package com.webest.store.store.application.context;

import com.webest.store.store.domain.strategy.StoreStrategy;
import com.webest.store.store.exception.StoreErrorCode;
import com.webest.store.store.exception.StoreException;
import com.webest.store.store.presentation.dto.StoreResponse;
import com.webest.web.common.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreStrategyContext {
    private final List<StoreStrategy> storeStrategies;

    public List<StoreResponse> getDeliveryStores(String userId, UserRole role) {
        return storeStrategies.stream()
                .filter(storeStrategy -> storeStrategy.checkUserRole(role))
                .findAny()
                .orElseThrow(() -> new StoreException(StoreErrorCode.UNAUTHORIZED_ACCESS))
                .getDeliveryStores(userId);
    }


    public List<StoreResponse> searchDeliveryStores(String userId, UserRole role, Long categoryId, String keyword) {
        return storeStrategies.stream()
                .filter(storeStrategy -> storeStrategy.checkUserRole(role))
                .findAny()
                .orElseThrow(() -> new StoreException(StoreErrorCode.UNAUTHORIZED_ACCESS))
                .searchDeliveryStores(userId, keyword, categoryId);
    }

    public List<StoreResponse> getTakeOutStores(String userId, UserRole role, Double radius) {
        return storeStrategies.stream()
                .filter(storeStrategy -> storeStrategy.checkUserRole(role))
                .findAny()
                .orElseThrow(() -> new StoreException(StoreErrorCode.UNAUTHORIZED_ACCESS))
                .getTakeOutStores(userId, radius);
    }
}
