package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.condition;

import static com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.QLegacyConversionOutboxJpaEntity.legacyConversionOutboxJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.LegacyConversionOutboxJpaEntity;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * LegacyConversionOutboxConditionBuilder - QueryDSL 조건 빌더.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Component
public class LegacyConversionOutboxConditionBuilder {

    public BooleanExpression legacyProductGroupIdEq(Long legacyProductGroupId) {
        return legacyProductGroupId != null
                ? legacyConversionOutboxJpaEntity.legacyProductGroupId.eq(legacyProductGroupId)
                : null;
    }

    public BooleanExpression statusPending() {
        return legacyConversionOutboxJpaEntity.status.eq(
                LegacyConversionOutboxJpaEntity.Status.PENDING);
    }

    public BooleanExpression statusProcessing() {
        return legacyConversionOutboxJpaEntity.status.eq(
                LegacyConversionOutboxJpaEntity.Status.PROCESSING);
    }

    public BooleanExpression retryCountLtMaxRetry() {
        return legacyConversionOutboxJpaEntity.retryCount.lt(
                legacyConversionOutboxJpaEntity.maxRetry);
    }

    public BooleanExpression createdAtBefore(Instant beforeTime) {
        return beforeTime != null ? legacyConversionOutboxJpaEntity.createdAt.lt(beforeTime) : null;
    }

    public BooleanExpression updatedAtBefore(Instant beforeTime) {
        return beforeTime != null ? legacyConversionOutboxJpaEntity.updatedAt.lt(beforeTime) : null;
    }
}
