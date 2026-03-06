package com.ryuqq.marketplace.adapter.out.persistence.outboundseller.condition;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.outboundseller.entity.OutboundSellerOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.outboundseller.entity.QOutboundSellerOutboxJpaEntity;
import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class OutboundSellerOutboxConditionBuilder {

    private static final QOutboundSellerOutboxJpaEntity outbox =
            QOutboundSellerOutboxJpaEntity.outboundSellerOutboxJpaEntity;

    public BooleanExpression idEq(Long id) {
        return outbox.id.eq(id);
    }

    public BooleanExpression statusEq(OutboundSellerOutboxJpaEntity.Status status) {
        return outbox.status.eq(status);
    }

    public BooleanExpression statusPending() {
        return statusEq(OutboundSellerOutboxJpaEntity.Status.PENDING);
    }

    public BooleanExpression statusProcessing() {
        return statusEq(OutboundSellerOutboxJpaEntity.Status.PROCESSING);
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
