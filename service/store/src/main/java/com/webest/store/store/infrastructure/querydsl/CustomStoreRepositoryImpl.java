package com.webest.store.store.infrastructure.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import static com.webest.store.store.domain.model.QStore.store;
import com.webest.store.store.domain.model.Store;
import com.webest.store.store.domain.repository.CustomStoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class CustomStoreRepositoryImpl implements CustomStoreRepository {

    private final JPAQueryFactory queryFactory;

    // 법정 코드로 상점 검색
    @Override
    public List<Store> findStoresByAddressCode(Long addressCode) {
        return queryFactory.selectFrom(store)
                .where(store.addressCodeList.contains(addressCode))
                .fetch(); // 쿼리 결과를 리스트 형태로 반환
    }

    // 법정코드 & 카테고리별 검색
    @Override
    public List<Store> findStoresByAddressCodeAndCategoryIdAndName(Long addressCode, Long categoryId, String keyword) {
        return queryFactory
                .selectFrom(store)
                .where(
                        store.addressCodeList.contains(addressCode),
                        nameContains(keyword),
                        categoryEq(categoryId)
                )
                .fetch();
    }

    // 카테고리별 검색
    @Override
    public List<Store> findStoresByCategoryIdAndName(Long categoryId, String keyword) {
        return queryFactory
                .selectFrom(store)
                .where(
                        nameContains(keyword),
                        categoryEq(categoryId)
                )
                .fetch();
    }

    // 이름이 포함된 상점 필터링
    private BooleanExpression nameContains(String keyword) {
        return keyword != null ? store.name.containsIgnoreCase(keyword) : null;
    }

    // 카테고리 필터링
    private BooleanExpression categoryEq(Long categoryId) {
        return categoryId != null && categoryId != 0 ? store.categoryId.eq(categoryId) : null;
    }


}
