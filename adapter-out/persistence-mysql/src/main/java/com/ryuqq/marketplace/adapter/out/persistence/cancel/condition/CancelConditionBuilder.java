package com.ryuqq.marketplace.adapter.out.persistence.cancel.condition;

import static com.ryuqq.marketplace.adapter.out.persistence.order.entity.QOrderItemJpaEntity.orderItemJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.order.entity.QOrderJpaEntity.orderJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.ryuqq.marketplace.adapter.out.persistence.cancel.entity.QCancelJpaEntity;
import com.ryuqq.marketplace.domain.cancel.query.CancelDateField;
import com.ryuqq.marketplace.domain.cancel.query.CancelSearchCriteria;
import com.ryuqq.marketplace.domain.cancel.vo.CancelStatus;
import com.ryuqq.marketplace.domain.cancel.vo.CancelType;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Cancel QueryDSL 조건 빌더.
 *
 * <p>CancelSearchCriteria 기반의 동적 쿼리 조건을 생성합니다.
 */
@Component
public class CancelConditionBuilder {

    private static final QCancelJpaEntity cancel = QCancelJpaEntity.cancelJpaEntity;

    public BooleanExpression idEq(String id) {
        return id != null ? cancel.id.eq(id) : null;
    }

    public BooleanExpression idIn(List<String> ids) {
        return ids != null && !ids.isEmpty() ? cancel.id.in(ids) : null;
    }

    public BooleanExpression sellerIdEq(Long sellerId) {
        return sellerId != null ? cancel.sellerId.eq(sellerId) : null;
    }

    public BooleanExpression orderItemIdEq(String orderItemId) {
        return cancel.orderItemId.eq(orderItemId);
    }

    public BooleanExpression orderItemIdIn(List<String> orderItemIds) {
        return orderItemIds != null && !orderItemIds.isEmpty()
                ? cancel.orderItemId.in(orderItemIds)
                : null;
    }

    public BooleanExpression statusIn(CancelSearchCriteria criteria) {
        if (!criteria.hasStatusFilter()) {
            return null;
        }
        List<String> statusNames = criteria.statuses().stream().map(CancelStatus::name).toList();
        return cancel.cancelStatus.in(statusNames);
    }

    public BooleanExpression typeIn(CancelSearchCriteria criteria) {
        if (!criteria.hasTypeFilter()) {
            return null;
        }
        List<String> typeNames = criteria.types().stream().map(CancelType::name).toList();
        return cancel.cancelType.in(typeNames);
    }

    public BooleanExpression searchCondition(CancelSearchCriteria criteria) {
        if (!criteria.hasSearchCondition()) {
            return null;
        }
        String word = "%" + criteria.searchWord() + "%";
        if (!criteria.hasSearchField()) {
            return cancel.cancelNumber.like(word);
        }
        return switch (criteria.searchField()) {
            case CANCEL_NUMBER -> cancel.cancelNumber.like(word);
            case ORDER_NUMBER ->
                    cancel.orderItemId.in(
                            JPAExpressions.select(orderItemJpaEntity.id)
                                    .from(orderItemJpaEntity)
                                    .join(orderJpaEntity)
                                    .on(orderItemJpaEntity.orderId.eq(orderJpaEntity.id))
                                    .where(orderJpaEntity.orderNumber.like(word)));
            case CUSTOMER_NAME ->
                    cancel.orderItemId.in(
                            JPAExpressions.select(orderItemJpaEntity.id)
                                    .from(orderItemJpaEntity)
                                    .join(orderJpaEntity)
                                    .on(orderItemJpaEntity.orderId.eq(orderJpaEntity.id))
                                    .where(orderJpaEntity.buyerName.like(word)));
            case CUSTOMER_PHONE ->
                    cancel.orderItemId.in(
                            JPAExpressions.select(orderItemJpaEntity.id)
                                    .from(orderItemJpaEntity)
                                    .join(orderJpaEntity)
                                    .on(orderItemJpaEntity.orderId.eq(orderJpaEntity.id))
                                    .where(orderJpaEntity.buyerPhone.like(word)));
            case PRODUCT_NAME ->
                    cancel.orderItemId.in(
                            JPAExpressions.select(orderItemJpaEntity.id)
                                    .from(orderItemJpaEntity)
                                    .where(orderItemJpaEntity.externalProductName.like(word)));
        };
    }

    public BooleanExpression dateRange(CancelSearchCriteria criteria) {
        if (!criteria.hasDateRange()) {
            return null;
        }
        CancelDateField dateField =
                criteria.dateField() != null ? criteria.dateField() : CancelDateField.REQUESTED;

        var path =
                switch (dateField) {
                    case REQUESTED -> cancel.requestedAt;
                    case COMPLETED -> cancel.completedAt;
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
