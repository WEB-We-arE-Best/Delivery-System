package com.webest.store.store.domain.strategy;

import com.webest.store.store.application.GeoOperation;
import com.webest.store.store.domain.repository.CustomStoreRepository;
import com.webest.store.store.domain.repository.StoreRepository;
import com.webest.store.store.infrastructure.user.UserClient;
import com.webest.store.store.infrastructure.user.dto.UserResponse;
import com.webest.store.store.presentation.dto.StoreResponse;
import com.webest.web.common.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class UserStoreStrategy implements StoreStrategy {

    private final StoreRepository storeRepository;
    private final CustomStoreRepository customStoreRepository;
    private final UserClient userClient;
    private final GeoOperation geoOperation;

    @Override
    public List<StoreResponse> getDeliveryStores(String userId) {
        UserResponse user = userClient.getUser(userId).getData();
        return customStoreRepository.findStoresByAddressCode(user.addressCode())
                .stream()
                .map(StoreResponse::of)
                .toList();
    }

    // 배달 상점 검색 & 카테고리별 조회
    @Override
    public List<StoreResponse> searchDeliveryStores(String userId, String keyword, Long categoryId) {
        UserResponse user = userClient.getUser(userId).getData();
        return customStoreRepository.findStoresByAddressCodeAndCategoryIdAndName(user.addressCode(), categoryId, keyword)
                .stream()
                .map(StoreResponse::of)
                .toList();
    }

    @Override
    public List<StoreResponse> getTakeOutStores(String userId, Double radius) {
        UserResponse user = userClient.getUser(userId).getData();
        List<String> storeIds = geoOperation.findNearByStores(user.longitude(), user.latitude(), radius);
        return storeIds.stream()
                .map(storeId -> storeRepository.findById(Long.parseLong(storeId))
                        .map(StoreResponse::of)
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public boolean checkUserRole(UserRole role) {
        return role == UserRole.USER || role == UserRole.OWNER;
    }
}
