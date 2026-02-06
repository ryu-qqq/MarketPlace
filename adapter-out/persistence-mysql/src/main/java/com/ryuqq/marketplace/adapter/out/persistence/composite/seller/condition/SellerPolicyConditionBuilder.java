package com.ryuqq.marketplace.adapter.out.persistence.composite.seller.condition;

import static com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.entity.QRefundPolicyJpaEntity.refundPolicyJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.entity.QShippingPolicyJpaEntity.shippingPolicyJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * SellerPolicyConditionBuilder - 셀러 정책 Composite QueryDSL 조건 빌더.
 *
 * <p>ShippingPolicy + RefundPolicy 조회 쿼리용 조건.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Component
public class SellerPolicyConditionBuilder {

    // ===== ShippingPolicy 조건 =====

    public BooleanExpression shippingPolicySellerIdEq(Long sellerId) {
        return sellerId != null ? shippingPolicyJpaEntity.sellerId.eq(sellerId) : null;
    }

    public BooleanExpression shippingPolicySellerIdIn(List<Long> sellerIds) {
        return sellerIds != null && !sellerIds.isEmpty()
                ? shippingPolicyJpaEntity.sellerId.in(sellerIds)
                : null;
    }

    public BooleanExpression shippingPolicyNotDeleted() {
        return shippingPolicyJpaEntity.deletedAt.isNull();
    }

    public BooleanExpression shippingPolicyActiveEq(Boolean active) {
        return active != null ? shippingPolicyJpaEntity.active.eq(active) : null;
    }

    public BooleanExpression shippingPolicyDefaultOnly() {
        return shippingPolicyJpaEntity.defaultPolicy.isTrue();
    }

    // ===== RefundPolicy 조건 =====

    public BooleanExpression refundPolicySellerIdEq(Long sellerId) {
        return sellerId != null ? refundPolicyJpaEntity.sellerId.eq(sellerId) : null;
    }

    public BooleanExpression refundPolicySellerIdIn(List<Long> sellerIds) {
        return sellerIds != null && !sellerIds.isEmpty()
                ? refundPolicyJpaEntity.sellerId.in(sellerIds)
                : null;
    }

    public BooleanExpression refundPolicyNotDeleted() {
        return refundPolicyJpaEntity.deletedAt.isNull();
    }

    public BooleanExpression refundPolicyActiveEq(Boolean active) {
        return active != null ? refundPolicyJpaEntity.active.eq(active) : null;
    }

    public BooleanExpression refundPolicyDefaultOnly() {
        return refundPolicyJpaEntity.defaultPolicy.isTrue();
    }
}
