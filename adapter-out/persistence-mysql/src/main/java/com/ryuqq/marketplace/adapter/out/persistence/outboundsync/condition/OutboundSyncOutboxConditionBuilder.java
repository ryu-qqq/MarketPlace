package com.ryuqq.marketplace.adapter.out.persistence.outboundsync.condition;

import static com.ryuqq.marketplace.adapter.out.persistence.outboundsync.entity.QOutboundSyncOutboxJpaEntity.outboundSyncOutboxJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.entity.OutboundSyncOutboxJpaEntity;
import java.time.Instant;
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
}
