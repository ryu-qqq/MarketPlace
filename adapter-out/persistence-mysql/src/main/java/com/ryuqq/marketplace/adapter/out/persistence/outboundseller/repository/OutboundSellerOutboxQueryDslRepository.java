package com.ryuqq.marketplace.adapter.out.persistence.outboundseller.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.outboundseller.condition.OutboundSellerOutboxConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.outboundseller.entity.OutboundSellerOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.outboundseller.entity.QOutboundSellerOutboxJpaEntity;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class OutboundSellerOutboxQueryDslRepository {

    private static final QOutboundSellerOutboxJpaEntity outbox =
            QOutboundSellerOutboxJpaEntity.outboundSellerOutboxJpaEntity;

    private final JPAQueryFactory queryFactory;
    private final OutboundSellerOutboxConditionBuilder conditionBuilder;

    public OutboundSellerOutboxQueryDslRepository(
            JPAQueryFactory queryFactory, OutboundSellerOutboxConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    public Optional<OutboundSellerOutboxJpaEntity> findById(Long id) {
        OutboundSellerOutboxJpaEntity entity =
                queryFactory.selectFrom(outbox).where(conditionBuilder.idEq(id)).fetchOne();
        return Optional.ofNullable(entity);
    }

    public List<OutboundSellerOutboxJpaEntity> findPendingForRetry(Instant beforeTime, int limit) {
        return queryFactory
                .selectFrom(outbox)
                .where(
                        conditionBuilder.statusPending(),
                        conditionBuilder.retryCountLtMaxRetry(),
                        conditionBuilder.createdAtBefore(beforeTime))
                .orderBy(outbox.createdAt.asc())
                .limit(limit)
                .fetch();
    }

    public List<OutboundSellerOutboxJpaEntity> findProcessingTimeout(
            Instant timeoutThreshold, int limit) {
        return queryFactory
                .selectFrom(outbox)
                .where(
                        conditionBuilder.statusProcessing(),
                        conditionBuilder.updatedAtBefore(timeoutThreshold))
                .orderBy(outbox.updatedAt.asc())
                .limit(limit)
                .fetch();
    }
}
