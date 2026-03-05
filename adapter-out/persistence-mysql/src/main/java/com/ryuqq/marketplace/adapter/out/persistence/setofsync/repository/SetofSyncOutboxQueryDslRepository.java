package com.ryuqq.marketplace.adapter.out.persistence.setofsync.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.setofsync.condition.SetofSyncOutboxConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.setofsync.entity.QSetofSyncOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.setofsync.entity.SetofSyncOutboxJpaEntity;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class SetofSyncOutboxQueryDslRepository {

    private static final QSetofSyncOutboxJpaEntity outbox =
            QSetofSyncOutboxJpaEntity.setofSyncOutboxJpaEntity;

    private final JPAQueryFactory queryFactory;
    private final SetofSyncOutboxConditionBuilder conditionBuilder;

    public SetofSyncOutboxQueryDslRepository(
            JPAQueryFactory queryFactory, SetofSyncOutboxConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    public Optional<SetofSyncOutboxJpaEntity> findById(Long id) {
        SetofSyncOutboxJpaEntity entity =
                queryFactory.selectFrom(outbox).where(conditionBuilder.idEq(id)).fetchOne();
        return Optional.ofNullable(entity);
    }

    public List<SetofSyncOutboxJpaEntity> findPendingForRetry(Instant beforeTime, int limit) {
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

    public List<SetofSyncOutboxJpaEntity> findProcessingTimeout(
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
