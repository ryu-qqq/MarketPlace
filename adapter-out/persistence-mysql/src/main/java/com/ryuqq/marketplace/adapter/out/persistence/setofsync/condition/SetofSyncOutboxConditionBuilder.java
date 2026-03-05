package com.ryuqq.marketplace.adapter.out.persistence.setofsync.condition;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.setofsync.entity.QSetofSyncOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.setofsync.entity.SetofSyncOutboxJpaEntity;
import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class SetofSyncOutboxConditionBuilder {

    private static final QSetofSyncOutboxJpaEntity outbox =
            QSetofSyncOutboxJpaEntity.setofSyncOutboxJpaEntity;

    public BooleanExpression idEq(Long id) {
        return outbox.id.eq(id);
    }

    public BooleanExpression statusEq(SetofSyncOutboxJpaEntity.Status status) {
        return outbox.status.eq(status);
    }

    public BooleanExpression statusPending() {
        return statusEq(SetofSyncOutboxJpaEntity.Status.PENDING);
    }

    public BooleanExpression statusProcessing() {
        return statusEq(SetofSyncOutboxJpaEntity.Status.PROCESSING);
    }

    public BooleanExpression retryCountLtMaxRetry() {
        return outbox.retryCount.lt(outbox.maxRetry);
    }

    public BooleanExpression createdAtBefore(Instant instant) {
        return outbox.createdAt.before(instant);
    }

    public BooleanExpression updatedAtBefore(Instant instant) {
        return outbox.updatedAt.before(instant);
    }
}
