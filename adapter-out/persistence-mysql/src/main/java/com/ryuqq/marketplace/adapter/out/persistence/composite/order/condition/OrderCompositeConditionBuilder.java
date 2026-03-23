package com.ryuqq.marketplace.adapter.out.persistence.composite.order.condition;

import static com.ryuqq.marketplace.adapter.out.persistence.order.entity.QOrderItemHistoryJpaEntity.orderItemHistoryJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.order.entity.QOrderItemJpaEntity.orderItemJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.order.entity.QOrderJpaEntity.orderJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.order.entity.QPaymentJpaEntity.paymentJpaEntity;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.ryuqq.marketplace.domain.order.query.OrderDateField;
import com.ryuqq.marketplace.domain.order.query.OrderSearchCriteria;
import com.ryuqq.marketplace.domain.order.query.OrderSearchField;
import com.ryuqq.marketplace.domain.order.query.OrderSortKey;
import java.time.Instant;
import org.springframework.stereotype.Component;

/** Order Composite QueryDSL 조건 빌더. */
@Component
public class OrderCompositeConditionBuilder {

    // ===== JOIN 조건 =====

    public BooleanExpression paymentJoinCondition() {
        return paymentJpaEntity.orderId.eq(orderJpaEntity.id);
    }

    /** order_items ↔ orders 조인 조건. */
    public BooleanExpression itemOrderJoinCondition() {
        return orderItemJpaEntity.orderId.eq(orderJpaEntity.id);
    }

    public BooleanExpression itemOrderIdEq(String orderId) {
        return orderItemJpaEntity.orderId.eq(orderId);
    }

    /** order_item_histories ↔ order_items 조인 조건 (orderItemId 기준). */
    public BooleanExpression itemHistoryOrderItemIdEq(String orderItemId) {
        return orderItemHistoryJpaEntity.orderItemId.eq(orderItemId);
    }

    // ===== 필터 조건 =====

    public BooleanExpression orderNotDeleted() {
        return orderJpaEntity.deletedAt.isNull();
    }

    public BooleanExpression orderIdEq(String orderId) {
        return orderId != null ? orderJpaEntity.id.eq(orderId) : null;
    }

    public BooleanExpression searchCondition(OrderSearchField field, String word) {
        if (field == null || word == null || word.isBlank()) {
            return null;
        }
        String trimmed = word.trim();
        return switch (field) {
            case ORDER_ID -> orderItemJpaEntity.orderItemNumber.eq(trimmed);
            case PAYMENT_ID ->
                    orderJpaEntity.id.in(
                            JPAExpressions.select(paymentJpaEntity.orderId)
                                    .from(paymentJpaEntity)
                                    .where(paymentJpaEntity.paymentNumber.eq(trimmed)));
            case PRODUCT_GROUP_ID -> orderItemJpaEntity.productGroupId.eq(Long.parseLong(trimmed));
            case BUYER_NAME -> orderJpaEntity.buyerName.containsIgnoreCase(trimmed);
        };
    }

    public BooleanExpression dateRangeCondition(
            OrderDateField dateField, Instant start, Instant end) {
        if (dateField == null || start == null || end == null) {
            return null;
        }
        return switch (dateField) {
            case ORDERED -> orderJpaEntity.externalOrderedAt.between(start, end);
            case SHIPPED, DELIVERED -> orderJpaEntity.createdAt.between(start, end);
        };
    }

    /** Criteria 기반 복합 조건 생성. */
    public BooleanExpression[] buildWhereConditions(OrderSearchCriteria criteria) {
        return new BooleanExpression[] {
            orderNotDeleted(),
            criteria.hasSearchCondition()
                    ? searchCondition(criteria.searchField(), criteria.searchWord())
                    : null,
            criteria.hasDateRange()
                    ? dateRangeCondition(
                            criteria.dateField(),
                            criteria.dateRange().startInstant(),
                            criteria.dateRange().endInstant())
                    : null
        };
    }

    /** 정렬 조건 생성. */
    public OrderSpecifier<?> buildOrderSpecifier(OrderSearchCriteria criteria) {
        OrderSortKey sortKey = criteria.queryContext().sortKey();
        boolean asc = criteria.queryContext().isAscending();

        return switch (sortKey) {
            case CREATED_AT ->
                    asc ? orderJpaEntity.createdAt.asc() : orderJpaEntity.createdAt.desc();
            case ORDERED_AT ->
                    asc
                            ? orderJpaEntity.externalOrderedAt.asc()
                            : orderJpaEntity.externalOrderedAt.desc();
            case UPDATED_AT ->
                    asc ? orderJpaEntity.updatedAt.asc() : orderJpaEntity.updatedAt.desc();
        };
    }
}
