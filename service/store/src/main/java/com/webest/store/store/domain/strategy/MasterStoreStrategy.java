package com.webest.store.store.domain.strategy;

import com.webest.store.store.domain.model.Store;
import com.webest.store.store.domain.repository.CustomStoreRepository;
import com.webest.store.store.domain.repository.StoreRepository;
import com.webest.store.store.presentation.dto.StoreResponse;
import com.webest.web.common.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MasterStoreStrategy implements StoreStrategy {

    private final StoreRepository storeRepository;
    private final CustomStoreRepository customStoreRepository;

    @Override
    public List<StoreResponse> getDeliveryStores(String userId) {
        List<Store> stores = storeRepository.findAll();
        return stores.stream()
                .map(StoreResponse::of)
                .toList();
    }

    @Override
    public List<StoreResponse> searchDeliveryStores(String userId, String keyword, Long categoryId) {
        List<Store> stores = customStoreRepository.findStoresByCategoryIdAndName(categoryId, keyword);
        return stores.stream()
                .map(StoreResponse::of)
                .toList();
    }

    @Override
    public List<StoreResponse> getTakeOutStores(String userId, Double radius) {
        List<Store> stores = storeRepository.findAll();
        return stores.stream()
                .map(StoreResponse::of)
                .toList();
    }

    @Override
    public boolean checkUserRole(UserRole role) {
        return role == UserRole.MASTER;
    }
}
