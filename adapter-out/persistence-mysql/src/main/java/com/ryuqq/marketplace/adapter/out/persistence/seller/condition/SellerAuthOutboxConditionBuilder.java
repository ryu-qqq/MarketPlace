package com.ryuqq.marketplace.adapter.out.persistence.seller.condition;

import static com.ryuqq.marketplace.adapter.out.persistence.seller.entity.QSellerAuthOutboxJpaEntity.sellerAuthOutboxJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerAuthOutboxJpaEntity;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * SellerAuthOutboxConditionBuilder - 셀러 인증 Outbox QueryDSL 조건 빌더.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Component
public class SellerAuthOutboxConditionBuilder {

    public BooleanExpression sellerIdEq(Long sellerId) {
        return sellerId != null ? sellerAuthOutboxJpaEntity.sellerId.eq(sellerId) : null;
    }

    public BooleanExpression statusEq(SellerAuthOutboxJpaEntity.Status status) {
        return status != null ? sellerAuthOutboxJpaEntity.status.eq(status) : null;
    }

    public BooleanExpression statusPending() {
        return sellerAuthOutboxJpaEntity.status.eq(SellerAuthOutboxJpaEntity.Status.PENDING);
    }

    public BooleanExpression statusProcessing() {
        return sellerAuthOutboxJpaEntity.status.eq(SellerAuthOutboxJpaEntity.Status.PROCESSING);
    }

    public BooleanExpression retryCountLtMaxRetry() {
        return sellerAuthOutboxJpaEntity.retryCount.lt(sellerAuthOutboxJpaEntity.maxRetry);
    }

    public BooleanExpression createdAtBefore(Instant beforeTime) {
        return beforeTime != null ? sellerAuthOutboxJpaEntity.createdAt.lt(beforeTime) : null;
    }

    public BooleanExpression updatedAtBefore(Instant beforeTime) {
        return beforeTime != null ? sellerAuthOutboxJpaEntity.updatedAt.lt(beforeTime) : null;
    }
}
