package com.ryuqq.marketplace.adapter.out.persistence.refundoutbox.condition;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.refundoutbox.entity.QRefundOutboxJpaEntity;
import java.time.Instant;
import org.springframework.stereotype.Component;

/** RefundOutbox QueryDSL 조건 빌더. */
@Component
public class RefundOutboxConditionBuilder {

    private static final QRefundOutboxJpaEntity outbox =
            QRefundOutboxJpaEntity.refundOutboxJpaEntity;

    public BooleanExpression statusPending() {
        return outbox.status.eq("PENDING");
    }

    public BooleanExpression statusProcessing() {
        return outbox.status.eq("PROCESSING");
    }

    public BooleanExpression createdAtBefore(Instant beforeTime) {
        return beforeTime != null ? outbox.createdAt.before(beforeTime) : null;
    }

    public BooleanExpression updatedAtBefore(Instant beforeTime) {
        return beforeTime != null ? outbox.updatedAt.before(beforeTime) : null;
    }
}
