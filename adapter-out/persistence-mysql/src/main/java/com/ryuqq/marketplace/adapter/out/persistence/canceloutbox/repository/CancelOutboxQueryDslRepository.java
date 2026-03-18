package com.ryuqq.marketplace.adapter.out.persistence.canceloutbox.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.canceloutbox.condition.CancelOutboxConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.canceloutbox.entity.CancelOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.canceloutbox.entity.QCancelOutboxJpaEntity;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Repository;

/** CancelOutbox QueryDSL Repository. */
@Repository
public class CancelOutboxQueryDslRepository {

    private static final QCancelOutboxJpaEntity outbox = QCancelOutboxJpaEntity.cancelOutboxJpaEntity;
    private static final long MAX_FETCH_SIZE = 1000L;

    private final JPAQueryFactory queryFactory;
    private final CancelOutboxConditionBuilder conditionBuilder;

    public CancelOutboxQueryDslRepository(
            JPAQueryFactory queryFactory, CancelOutboxConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    public List<CancelOutboxJpaEntity> findPendingOutboxes(Instant beforeTime, int batchSize) {
        long limit = Math.min(batchSize, MAX_FETCH_SIZE);
        return queryFactory
                .selectFrom(outbox)
                .where(conditionBuilder.statusPending(), conditionBuilder.createdAtBefore(beforeTime))
                .orderBy(outbox.createdAt.asc())
                .limit(limit)
                .fetch();
    }

    public List<CancelOutboxJpaEntity> findProcessingTimeoutOutboxes(
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
