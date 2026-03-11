package com.ryuqq.marketplace.adapter.out.persistence.inboundorder.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.inboundorder.entity.InboundOrderJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.inboundorder.entity.QInboundOrderJpaEntity;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** InboundOrder QueryDSL Repository — 모든 조회를 담당. */
@Repository
public class InboundOrderQueryDslRepository {

    private static final QInboundOrderJpaEntity inboundOrder =
            QInboundOrderJpaEntity.inboundOrderJpaEntity;

    private final JPAQueryFactory queryFactory;

    public InboundOrderQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public boolean existsBySalesChannelIdAndExternalOrderNo(
            long salesChannelId, String externalOrderNo) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(inboundOrder)
                        .where(
                                inboundOrder.salesChannelId.eq(salesChannelId),
                                inboundOrder.externalOrderNo.eq(externalOrderNo))
                        .fetchFirst();
        return result != null;
    }

    public List<String> findExternalOrderNosBySalesChannelIdAndExternalOrderNoIn(
            long salesChannelId, Collection<String> externalOrderNos) {
        return queryFactory
                .select(inboundOrder.externalOrderNo)
                .from(inboundOrder)
                .where(
                        inboundOrder.salesChannelId.eq(salesChannelId),
                        inboundOrder.externalOrderNo.in(externalOrderNos))
                .fetch();
    }

    public Optional<Instant> findMaxExternalOrderedAtBySalesChannelId(long salesChannelId) {
        Instant result =
                queryFactory
                        .select(inboundOrder.externalOrderedAt.max())
                        .from(inboundOrder)
                        .where(inboundOrder.salesChannelId.eq(salesChannelId))
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    public List<InboundOrderJpaEntity> findByStatusOrderByIdAsc(
            InboundOrderJpaEntity.Status status, int limit) {
        return queryFactory
                .selectFrom(inboundOrder)
                .where(inboundOrder.status.eq(status))
                .orderBy(inboundOrder.id.asc())
                .limit(limit)
                .fetch();
    }
}
