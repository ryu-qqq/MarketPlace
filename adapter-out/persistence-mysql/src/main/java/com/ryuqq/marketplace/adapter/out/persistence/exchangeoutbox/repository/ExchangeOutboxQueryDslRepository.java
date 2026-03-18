package com.ryuqq.marketplace.adapter.out.persistence.exchangeoutbox.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.exchangeoutbox.condition.ExchangeOutboxConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.exchangeoutbox.entity.ExchangeOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.exchangeoutbox.entity.QExchangeOutboxJpaEntity;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Repository;

/** ExchangeOutbox QueryDSL Repository. */
@Repository
public class ExchangeOutboxQueryDslRepository {

    private static final QExchangeOutboxJpaEntity outbox = QExchangeOutboxJpaEntity.exchangeOutboxJpaEntity;
    private static final long MAX_FETCH_SIZE = 1000L;

    private final JPAQueryFactory queryFactory;
    private final ExchangeOutboxConditionBuilder conditionBuilder;

    public ExchangeOutboxQueryDslRepository(
            JPAQueryFactory queryFactory, ExchangeOutboxConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    public List<ExchangeOutboxJpaEntity> findPendingOutboxes(Instant beforeTime, int batchSize) {
        long limit = Math.min(batchSize, MAX_FETCH_SIZE);
        return queryFactory
                .selectFrom(outbox)
                .where(conditionBuilder.statusPending(), conditionBuilder.createdAtBefore(beforeTime))
                .orderBy(outbox.createdAt.asc())
                .limit(limit)
                .fetch();
    }

    public List<ExchangeOutboxJpaEntity> findProcessingTimeoutOutboxes(
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
