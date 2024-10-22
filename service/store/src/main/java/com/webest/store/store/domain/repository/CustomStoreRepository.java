package com.webest.store.store.domain.repository;

import com.webest.store.store.domain.model.Store;

import java.util.List;

public interface CustomStoreRepository {
    List<Store> findStoresByAddressCode(Long addressCode);
    List<Store> findStoresByAddressCodeAndCategoryIdAndName(Long addressCode, Long categoryId, String keyword);

    // 카테고리별 검색
    List<Store> findStoresByCategoryIdAndName(Long categoryId, String keyword);
}
