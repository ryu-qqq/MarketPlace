package com.ryuqq.marketplace.adapter.out.persistence.refundoutbox.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.refundoutbox.condition.RefundOutboxConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.refundoutbox.entity.QRefundOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.refundoutbox.entity.RefundOutboxJpaEntity;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Repository;

/** RefundOutbox QueryDSL Repository. */
@Repository
public class RefundOutboxQueryDslRepository {

    private static final QRefundOutboxJpaEntity outbox =
            QRefundOutboxJpaEntity.refundOutboxJpaEntity;
    private static final long MAX_FETCH_SIZE = 1000L;

    private final JPAQueryFactory queryFactory;
    private final RefundOutboxConditionBuilder conditionBuilder;

    public RefundOutboxQueryDslRepository(
            JPAQueryFactory queryFactory, RefundOutboxConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    public List<RefundOutboxJpaEntity> findPendingOutboxes(Instant beforeTime, int batchSize) {
        long limit = Math.min(batchSize, MAX_FETCH_SIZE);
        return queryFactory
                .selectFrom(outbox)
                .where(
                        conditionBuilder.statusPending(),
                        conditionBuilder.createdAtBefore(beforeTime))
                .orderBy(outbox.createdAt.asc())
                .limit(limit)
                .fetch();
    }

    public List<RefundOutboxJpaEntity> findProcessingTimeoutOutboxes(
            Instant timeoutBefore, int batchSize) {
        long limit = Math.min(batchSize, MAX_FETCH_SIZE);
        return queryFactory
                .selectFrom(outbox)
                .where(
                        conditionBuilder.statusProcessing(),
                        conditionBuilder.updatedAtBefore(timeoutBefore))
                .orderBy(outbox.updatedAt.asc())
                .limit(limit)
                .fetch();
    }
}
