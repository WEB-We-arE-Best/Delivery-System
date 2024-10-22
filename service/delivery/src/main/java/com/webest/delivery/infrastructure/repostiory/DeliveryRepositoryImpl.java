package com.webest.delivery.infrastructure.repostiory;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.webest.delivery.application.dtos.DeliveryDto;
import com.webest.delivery.domain.model.Delivery;
import com.webest.delivery.domain.model.QDelivery;
import com.webest.delivery.domain.repository.DeliveryCustomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DeliveryRepositoryImpl implements DeliveryCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Delivery> searchDelivery(DeliveryDto request, Pageable pageable) {

        QDelivery delivery = QDelivery.delivery;

        BooleanBuilder builder = new BooleanBuilder();

        // 조건: orderId
        if (request.orderId() != null) {
            builder.and(delivery.orderId.eq(request.orderId()));
        }

        // 조건: riderId
        if (request.riderId() != null) {
            builder.and(delivery.riderId.eq(request.riderId()));
        }

        // 조건: deliveryStatus
        if (request.deliveryStatus() != null) {
            builder.and(delivery.deliveryStatus.eq(request.deliveryStatus()));
        }

        // 조건: storeAddressCode
        if (request.storeAddressCode() != null) {
            builder.and(delivery.storeAddressCode.eq(request.storeAddressCode()));
        }

        // 조건: storeDetailAddress
        if (request.storeDetailAddress() != null) {
            builder.and(delivery.storeDetailAddress.containsIgnoreCase(request.storeDetailAddress()));
        }

        // 조건: arrivalAddressCode
        if (request.arrivalAddressCode() != null) {
            builder.and(delivery.arrivalAddressCode.eq(request.arrivalAddressCode()));
        }

        // 조건: arrivalDetailAddress
        if (request.arrivalDetailAddress() != null) {
            builder.and(delivery.arrivalDetailAddress.containsIgnoreCase(request.arrivalDetailAddress()));
        }

        // 조건: deliveryFeeAmount
        if (request.deliveryFeeAmount() != null) {
            builder.and(delivery.deliveryFeeAmount.eq(request.deliveryFeeAmount()));
        }


        // Query 생성
        JPAQuery<Delivery> query = jpaQueryFactory
                .selectFrom(delivery)
                .where(builder)
                .offset(pageable.getOffset())  // 페이징 처리
                .limit(pageable.getPageSize()); // 페이징 처리

        long total = query.fetchCount();  // 전체 검색 결과 수
        List<Delivery> results = query.fetch();  // 실제 검색 결과 목록

        return new PageImpl<>(results, pageable, total);

    }
}
