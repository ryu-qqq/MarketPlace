package com.ryuqq.marketplace.adapter.out.persistence.selleradmin.condition;

import static com.ryuqq.marketplace.adapter.out.persistence.selleradmin.entity.QSellerAdminAuthOutboxJpaEntity.sellerAdminAuthOutboxJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.entity.SellerAdminAuthOutboxJpaEntity;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * SellerAdminAuthOutboxConditionBuilder - 셀러 관리자 인증 Outbox QueryDSL 조건 빌더.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Component
public class SellerAdminAuthOutboxConditionBuilder {

    public BooleanExpression sellerAdminIdEq(String sellerAdminId) {
        return sellerAdminId != null
                ? sellerAdminAuthOutboxJpaEntity.sellerAdminId.eq(sellerAdminId)
                : null;
    }

    public BooleanExpression statusEq(SellerAdminAuthOutboxJpaEntity.Status status) {
        return status != null ? sellerAdminAuthOutboxJpaEntity.status.eq(status) : null;
    }

    public BooleanExpression statusPending() {
        return sellerAdminAuthOutboxJpaEntity.status.eq(
                SellerAdminAuthOutboxJpaEntity.Status.PENDING);
    }

    public BooleanExpression statusProcessing() {
        return sellerAdminAuthOutboxJpaEntity.status.eq(
                SellerAdminAuthOutboxJpaEntity.Status.PROCESSING);
    }

    public BooleanExpression retryCountLtMaxRetry() {
        return sellerAdminAuthOutboxJpaEntity.retryCount.lt(
                sellerAdminAuthOutboxJpaEntity.maxRetry);
    }

    public BooleanExpression createdAtBefore(Instant beforeTime) {
        return beforeTime != null ? sellerAdminAuthOutboxJpaEntity.createdAt.lt(beforeTime) : null;
    }

    public BooleanExpression updatedAtBefore(Instant beforeTime) {
        return beforeTime != null ? sellerAdminAuthOutboxJpaEntity.updatedAt.lt(beforeTime) : null;
    }
}
