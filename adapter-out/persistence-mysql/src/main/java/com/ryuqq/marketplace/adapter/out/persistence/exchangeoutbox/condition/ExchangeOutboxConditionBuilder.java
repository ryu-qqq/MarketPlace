package com.ryuqq.marketplace.adapter.out.persistence.exchangeoutbox.condition;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.exchangeoutbox.entity.QExchangeOutboxJpaEntity;
import java.time.Instant;
import org.springframework.stereotype.Component;

/** ExchangeOutbox QueryDSL 조건 빌더. */
@Component
public class ExchangeOutboxConditionBuilder {

    private static final QExchangeOutboxJpaEntity outbox = QExchangeOutboxJpaEntity.exchangeOutboxJpaEntity;

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
