package com.ryuqq.marketplace.adapter.out.persistence.externalproductsync.condition;

import static com.ryuqq.marketplace.adapter.out.persistence.externalproductsync.entity.QExternalProductSyncOutboxJpaEntity.externalProductSyncOutboxJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.externalproductsync.entity.ExternalProductSyncOutboxJpaEntity;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * ExternalProductSyncOutboxConditionBuilder - 외부 상품 연동 Outbox QueryDSL 조건 빌더.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Component
public class ExternalProductSyncOutboxConditionBuilder {

    public BooleanExpression productGroupIdEq(Long productGroupId) {
        return productGroupId != null
                ? externalProductSyncOutboxJpaEntity.productGroupId.eq(productGroupId)
                : null;
    }

    public BooleanExpression sellerIdEq(Long sellerId) {
        return sellerId != null ? externalProductSyncOutboxJpaEntity.sellerId.eq(sellerId) : null;
    }

    public BooleanExpression statusPending() {
        return externalProductSyncOutboxJpaEntity.status.eq(
                ExternalProductSyncOutboxJpaEntity.Status.PENDING);
    }

    public BooleanExpression statusProcessing() {
        return externalProductSyncOutboxJpaEntity.status.eq(
                ExternalProductSyncOutboxJpaEntity.Status.PROCESSING);
    }

    public BooleanExpression retryCountLtMaxRetry() {
        return externalProductSyncOutboxJpaEntity.retryCount.lt(
                externalProductSyncOutboxJpaEntity.maxRetry);
    }

    public BooleanExpression createdAtBefore(Instant beforeTime) {
        return beforeTime != null
                ? externalProductSyncOutboxJpaEntity.createdAt.lt(beforeTime)
                : null;
    }

    public BooleanExpression updatedAtBefore(Instant beforeTime) {
        return beforeTime != null
                ? externalProductSyncOutboxJpaEntity.updatedAt.lt(beforeTime)
                : null;
    }
}
