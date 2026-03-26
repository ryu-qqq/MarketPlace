package com.ryuqq.marketplace.adapter.out.persistence.outboundproductimage.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.outboundproductimage.entity.QOutboundProductImageJpaEntity.outboundProductImageJpaEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproductimage.entity.OutboundProductImageJpaEntity;
import java.util.List;
import org.springframework.stereotype.Repository;

/** OutboundProductImage QueryDSL 조회 레포지토리. */
@Repository
public class OutboundProductImageQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public OutboundProductImageQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public List<OutboundProductImageJpaEntity> findActiveByOutboundProductId(
            Long outboundProductId) {
        return queryFactory
                .selectFrom(outboundProductImageJpaEntity)
                .where(
                        outboundProductImageJpaEntity.outboundProductId.eq(outboundProductId),
                        outboundProductImageJpaEntity.deleted.eq(false))
                .orderBy(outboundProductImageJpaEntity.sortOrder.asc())
                .fetch();
    }
}
