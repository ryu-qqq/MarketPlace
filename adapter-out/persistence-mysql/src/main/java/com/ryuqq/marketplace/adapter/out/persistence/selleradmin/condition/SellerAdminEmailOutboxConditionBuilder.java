package com.ryuqq.marketplace.adapter.out.persistence.selleradmin.condition;

import static com.ryuqq.marketplace.adapter.out.persistence.selleradmin.entity.QSellerAdminEmailOutboxJpaEntity.sellerAdminEmailOutboxJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.entity.SellerAdminEmailOutboxJpaEntity;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * SellerAdminEmailOutboxConditionBuilder - 셀러 관리자 이메일 Outbox QueryDSL 조건 빌더.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Component
public class SellerAdminEmailOutboxConditionBuilder {

    public BooleanExpression sellerIdEq(Long sellerId) {
        return sellerId != null ? sellerAdminEmailOutboxJpaEntity.sellerId.eq(sellerId) : null;
    }

    public BooleanExpression statusEq(SellerAdminEmailOutboxJpaEntity.Status status) {
        return status != null ? sellerAdminEmailOutboxJpaEntity.status.eq(status) : null;
    }

    public BooleanExpression statusPending() {
        return sellerAdminEmailOutboxJpaEntity.status.eq(
                SellerAdminEmailOutboxJpaEntity.Status.PENDING);
    }

    public BooleanExpression statusProcessing() {
        return sellerAdminEmailOutboxJpaEntity.status.eq(
                SellerAdminEmailOutboxJpaEntity.Status.PROCESSING);
    }

    public BooleanExpression retryCountLtMaxRetry() {
        return sellerAdminEmailOutboxJpaEntity.retryCount.lt(
                sellerAdminEmailOutboxJpaEntity.maxRetry);
    }

    public BooleanExpression createdAtBefore(Instant beforeTime) {
        return beforeTime != null ? sellerAdminEmailOutboxJpaEntity.createdAt.lt(beforeTime) : null;
    }

    public BooleanExpression updatedAtBefore(Instant beforeTime) {
        return beforeTime != null ? sellerAdminEmailOutboxJpaEntity.updatedAt.lt(beforeTime) : null;
    }
}
