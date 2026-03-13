package com.ryuqq.marketplace.adapter.out.persistence.outboundsync.condition;

import static com.ryuqq.marketplace.adapter.out.persistence.outboundsync.entity.QOutboundSyncOutboxJpaEntity.outboundSyncOutboxJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.entity.OutboundSyncOutboxJpaEntity;
import java.time.Instant;
import java.util.Collection;
import org.springframework.stereotype.Component;

/**
 * OutboundSyncOutboxConditionBuilder - 외부 상품 연동 Outbox QueryDSL 조건 빌더.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Component
public class OutboundSyncOutboxConditionBuilder {

    public BooleanExpression productGroupIdEq(Long productGroupId) {
        return productGroupId != null
                ? outboundSyncOutboxJpaEntity.productGroupId.eq(productGroupId)
                : null;
    }

    public BooleanExpression productGroupIdIn(Collection<Long> productGroupIds) {
        return productGroupIds != null && !productGroupIds.isEmpty()
                ? outboundSyncOutboxJpaEntity.productGroupId.in(productGroupIds)
                : null;
    }

    public BooleanExpression shopIdEq(Long shopId) {
        return shopId != null ? outboundSyncOutboxJpaEntity.shopId.eq(shopId) : null;
    }

    public BooleanExpression sellerIdEq(Long sellerId) {
        return sellerId != null ? outboundSyncOutboxJpaEntity.sellerId.eq(sellerId) : null;
    }

    public BooleanExpression statusPending() {
        return outboundSyncOutboxJpaEntity.status.eq(OutboundSyncOutboxJpaEntity.Status.PENDING);
    }

    public BooleanExpression statusProcessing() {
        return outboundSyncOutboxJpaEntity.status.eq(OutboundSyncOutboxJpaEntity.Status.PROCESSING);
    }

    public BooleanExpression retryCountLtMaxRetry() {
        return outboundSyncOutboxJpaEntity.retryCount.lt(outboundSyncOutboxJpaEntity.maxRetry);
    }

    public BooleanExpression createdAtBefore(Instant beforeTime) {
        return beforeTime != null ? outboundSyncOutboxJpaEntity.createdAt.lt(beforeTime) : null;
    }

    public BooleanExpression updatedAtBefore(Instant beforeTime) {
        return beforeTime != null ? outboundSyncOutboxJpaEntity.updatedAt.lt(beforeTime) : null;
    }

    public BooleanExpression syncTypeEq(String syncType) {
        if (syncType == null) {
            return null;
        }
        return outboundSyncOutboxJpaEntity.syncType.eq(
                OutboundSyncOutboxJpaEntity.SyncType.valueOf(syncType));
    }

    public BooleanExpression statusPendingOrProcessing() {
        return outboundSyncOutboxJpaEntity.status.in(
                OutboundSyncOutboxJpaEntity.Status.PENDING,
                OutboundSyncOutboxJpaEntity.Status.PROCESSING);
    }

    public BooleanExpression statusEq(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        return outboundSyncOutboxJpaEntity.status.eq(
                OutboundSyncOutboxJpaEntity.Status.valueOf(status));
    }

    public BooleanExpression statusEqEnum(OutboundSyncOutboxJpaEntity.Status status) {
        if (status == null) {
            return null;
        }
        return outboundSyncOutboxJpaEntity.status.eq(status);
    }
}
