package com.ryuqq.marketplace.adapter.out.persistence.ordermapping.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.ordermapping.entity.ExternalOrderItemMappingJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.ordermapping.entity.QExternalOrderItemMappingJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** 외부 주문상품 매핑 QueryDSL Repository. */
@Repository
public class ExternalOrderItemMappingQueryDslRepository {

    private static final QExternalOrderItemMappingJpaEntity mapping =
            QExternalOrderItemMappingJpaEntity.externalOrderItemMappingJpaEntity;

    private final JPAQueryFactory queryFactory;

    public ExternalOrderItemMappingQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public Optional<ExternalOrderItemMappingJpaEntity>
            findBySalesChannelIdAndExternalProductOrderId(
                    long salesChannelId, String externalProductOrderId) {
        ExternalOrderItemMappingJpaEntity entity =
                queryFactory
                        .selectFrom(mapping)
                        .where(
                                mapping.salesChannelId.eq(salesChannelId),
                                mapping.externalProductOrderId.eq(externalProductOrderId))
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    public List<ExternalOrderItemMappingJpaEntity> findByOrderItemIdIn(List<String> orderItemIds) {
        return queryFactory.selectFrom(mapping).where(mapping.orderItemId.in(orderItemIds)).fetch();
    }
}
