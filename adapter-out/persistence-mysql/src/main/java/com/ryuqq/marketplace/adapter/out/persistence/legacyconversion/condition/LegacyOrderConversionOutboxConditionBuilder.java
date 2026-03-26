package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.condition;

import static com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.QLegacyOrderConversionOutboxJpaEntity.legacyOrderConversionOutboxJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.domain.legacyconversion.vo.LegacyConversionOutboxStatus;
import java.time.Instant;
import java.util.Collection;
import org.springframework.stereotype.Component;

/**
 * LegacyOrderConversionOutboxConditionBuilder - QueryDSL 조건 빌더.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Component
public class LegacyOrderConversionOutboxConditionBuilder {

    public BooleanExpression statusPending() {
        return legacyOrderConversionOutboxJpaEntity.status.eq(
                LegacyConversionOutboxStatus.PENDING.name());
    }

    public BooleanExpression statusProcessing() {
        return legacyOrderConversionOutboxJpaEntity.status.eq(
                LegacyConversionOutboxStatus.PROCESSING.name());
    }

    public BooleanExpression retryCountLtMaxRetry() {
        return legacyOrderConversionOutboxJpaEntity.retryCount.lt(
                legacyOrderConversionOutboxJpaEntity.maxRetry);
    }

    public BooleanExpression createdAtBefore(Instant beforeTime) {
        return beforeTime != null
                ? legacyOrderConversionOutboxJpaEntity.createdAt.lt(beforeTime)
                : null;
    }

    public BooleanExpression updatedAtBefore(Instant beforeTime) {
        return beforeTime != null
                ? legacyOrderConversionOutboxJpaEntity.updatedAt.lt(beforeTime)
                : null;
    }

    public BooleanExpression legacyOrderIdEq(Long legacyOrderId) {
        return legacyOrderId != null
                ? legacyOrderConversionOutboxJpaEntity.legacyOrderId.eq(legacyOrderId)
                : null;
    }

    public BooleanExpression legacyOrderIdIn(Collection<Long> ids) {
        return (ids != null && !ids.isEmpty())
                ? legacyOrderConversionOutboxJpaEntity.legacyOrderId.in(ids)
                : null;
    }
}
