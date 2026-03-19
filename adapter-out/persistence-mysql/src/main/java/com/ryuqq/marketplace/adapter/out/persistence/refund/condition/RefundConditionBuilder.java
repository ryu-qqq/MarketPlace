package com.ryuqq.marketplace.adapter.out.persistence.refund.condition;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.refund.entity.QRefundClaimJpaEntity;
import com.ryuqq.marketplace.domain.refund.query.RefundDateField;
import com.ryuqq.marketplace.domain.refund.query.RefundSearchCriteria;
import com.ryuqq.marketplace.domain.refund.vo.RefundStatus;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Refund QueryDSL 조건 빌더.
 *
 * <p>RefundSearchCriteria 기반의 동적 쿼리 조건을 생성합니다.
 */
@Component
public class RefundConditionBuilder {

    private static final QRefundClaimJpaEntity refundClaim =
            QRefundClaimJpaEntity.refundClaimJpaEntity;

    public BooleanExpression idEq(String id) {
        return id != null ? refundClaim.id.eq(id) : null;
    }

    public BooleanExpression idIn(List<String> ids) {
        return ids != null && !ids.isEmpty() ? refundClaim.id.in(ids) : null;
    }

    public BooleanExpression sellerIdEq(Long sellerId) {
        return sellerId != null ? refundClaim.sellerId.eq(sellerId) : null;
    }

    public BooleanExpression orderItemIdEq(String orderItemId) {
        return refundClaim.orderItemId.eq(orderItemId);
    }

    public BooleanExpression orderItemIdIn(List<String> orderItemIds) {
        return orderItemIds != null && !orderItemIds.isEmpty()
                ? refundClaim.orderItemId.in(orderItemIds)
                : null;
    }

    public BooleanExpression statusIn(RefundSearchCriteria criteria) {
        if (!criteria.hasStatusFilter()) {
            return null;
        }
        List<String> statusNames = criteria.statuses().stream().map(RefundStatus::name).toList();
        return refundClaim.refundStatus.in(statusNames);
    }

    public BooleanExpression holdFilter(RefundSearchCriteria criteria) {
        if (!criteria.hasHoldFilter()) {
            return null;
        }
        return criteria.isHold()
                ? refundClaim.holdReason.isNotNull()
                : refundClaim.holdReason.isNull();
    }

    public BooleanExpression searchCondition(RefundSearchCriteria criteria) {
        if (!criteria.hasSearchCondition()) {
            return null;
        }
        String word = "%" + criteria.searchWord() + "%";
        if (!criteria.hasSearchField()) {
            return refundClaim.claimNumber.like(word);
        }
        return switch (criteria.searchField()) {
            case CLAIM_NUMBER -> refundClaim.claimNumber.like(word);
            case ORDER_NUMBER -> refundClaim.claimNumber.like(word);
            case CUSTOMER_NAME, CUSTOMER_PHONE, PRODUCT_NAME -> refundClaim.claimNumber.like(word);
        };
    }

    public BooleanExpression dateRange(RefundSearchCriteria criteria) {
        if (!criteria.hasDateRange()) {
            return null;
        }
        RefundDateField dateField =
                criteria.dateField() != null ? criteria.dateField() : RefundDateField.REQUESTED;

        var path =
                switch (dateField) {
                    case REQUESTED -> refundClaim.requestedAt;
                    case COMPLETED -> refundClaim.completedAt;
                };

        BooleanExpression condition = null;
        if (criteria.dateRange().startInstant() != null) {
            condition = path.goe(criteria.dateRange().startInstant());
        }
        if (criteria.dateRange().endInstant() != null) {
            BooleanExpression endCondition = path.loe(criteria.dateRange().endInstant());
            condition = condition != null ? condition.and(endCondition) : endCondition;
        }
        return condition;
    }
}
