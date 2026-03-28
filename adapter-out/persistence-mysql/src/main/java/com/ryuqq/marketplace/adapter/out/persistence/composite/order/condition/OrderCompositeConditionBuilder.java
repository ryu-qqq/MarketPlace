package com.ryuqq.marketplace.adapter.out.persistence.composite.order.condition;

import static com.ryuqq.marketplace.adapter.out.persistence.cancel.entity.QCancelJpaEntity.cancelJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.exchange.entity.QExchangeClaimJpaEntity.exchangeClaimJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.order.entity.QOrderItemHistoryJpaEntity.orderItemHistoryJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.order.entity.QOrderItemJpaEntity.orderItemJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.order.entity.QOrderJpaEntity.orderJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.order.entity.QPaymentJpaEntity.paymentJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.refund.entity.QRefundClaimJpaEntity.refundClaimJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.shipment.entity.QShipmentJpaEntity.shipmentJpaEntity;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.ryuqq.marketplace.domain.order.query.OrderDateField;
import com.ryuqq.marketplace.domain.order.query.OrderSearchCriteria;
import com.ryuqq.marketplace.domain.order.query.OrderSearchField;
import com.ryuqq.marketplace.domain.order.query.OrderSortKey;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/** Order Composite QueryDSL 조건 빌더. */
@SuppressWarnings("PMD.GodClass")
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
    public BooleanExpression itemHistoryOrderItemIdEq(Long orderItemId) {
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
            shopIdEq(criteria),
            statusFilter(criteria),
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

    /** shopId 필터 조건. */
    public BooleanExpression shopIdEq(OrderSearchCriteria criteria) {
        return criteria.shopId() != null ? orderJpaEntity.shopId.eq(criteria.shopId()) : null;
    }

    /**
     * OrderItemStatus 필터와 CrossDomain 상태 필터를 OR로 조합.
     *
     * <p>OrderItemStatus에 해당하는 값은 order_items.order_item_status로 필터링하고, CrossDomain 상태(SHIPPED,
     * DELIVERED, CLAIM_IN_PROGRESS, REFUNDED, EXCHANGED)는 서브쿼리로 필터링합니다.
     */
    public BooleanExpression statusFilter(OrderSearchCriteria criteria) {
        BooleanExpression orderItemCondition = null;
        if (criteria.hasStatusFilter()) {
            List<String> names = criteria.statuses().stream().map(Enum::name).toList();
            orderItemCondition = orderItemJpaEntity.orderItemStatus.in(names);
        }

        BooleanExpression crossDomainCondition = null;
        if (criteria.hasCrossDomainStatusFilter()) {
            for (String status : criteria.crossDomainStatuses()) {
                BooleanExpression sub = buildCrossDomainSubQuery(status);
                if (sub != null) {
                    crossDomainCondition =
                            crossDomainCondition == null ? sub : crossDomainCondition.or(sub);
                }
            }
        }

        if (orderItemCondition != null && crossDomainCondition != null) {
            return orderItemCondition.or(crossDomainCondition);
        }
        return orderItemCondition != null ? orderItemCondition : crossDomainCondition;
    }

    private BooleanExpression buildCrossDomainSubQuery(String status) {
        return switch (status) {
            case "SHIPPED" ->
                    orderItemJpaEntity.id.in(
                            JPAExpressions.select(shipmentJpaEntity.orderItemId)
                                    .from(shipmentJpaEntity)
                                    .where(shipmentJpaEntity.status.eq("SHIPPED")));
            case "DELIVERED" ->
                    orderItemJpaEntity.id.in(
                            JPAExpressions.select(shipmentJpaEntity.orderItemId)
                                    .from(shipmentJpaEntity)
                                    .where(shipmentJpaEntity.status.eq("DELIVERED")));
            case "CLAIM_IN_PROGRESS" ->
                    orderItemJpaEntity
                            .id
                            .in(
                                    JPAExpressions.select(cancelJpaEntity.orderItemId)
                                            .from(cancelJpaEntity)
                                            .where(cancelJpaEntity.cancelStatus.eq("REQUESTED")))
                            .or(
                                    orderItemJpaEntity.id.in(
                                            JPAExpressions.select(refundClaimJpaEntity.orderItemId)
                                                    .from(refundClaimJpaEntity)
                                                    .where(
                                                            refundClaimJpaEntity.refundStatus.in(
                                                                    "REQUESTED", "COLLECTING"))))
                            .or(
                                    orderItemJpaEntity.id.in(
                                            JPAExpressions.select(
                                                            exchangeClaimJpaEntity.orderItemId)
                                                    .from(exchangeClaimJpaEntity)
                                                    .where(
                                                            exchangeClaimJpaEntity.exchangeStatus
                                                                    .in(
                                                                            "REQUESTED",
                                                                            "COLLECTING",
                                                                            "COLLECTED",
                                                                            "RESHIPPING"))));
            case "REFUNDED" ->
                    orderItemJpaEntity
                            .id
                            .in(
                                    JPAExpressions.select(cancelJpaEntity.orderItemId)
                                            .from(cancelJpaEntity)
                                            .where(cancelJpaEntity.cancelStatus.eq("COMPLETED")))
                            .or(
                                    orderItemJpaEntity.id.in(
                                            JPAExpressions.select(refundClaimJpaEntity.orderItemId)
                                                    .from(refundClaimJpaEntity)
                                                    .where(
                                                            refundClaimJpaEntity.refundStatus.eq(
                                                                    "COMPLETED"))));
            case "EXCHANGED" ->
                    orderItemJpaEntity.id.in(
                            JPAExpressions.select(exchangeClaimJpaEntity.orderItemId)
                                    .from(exchangeClaimJpaEntity)
                                    .where(exchangeClaimJpaEntity.exchangeStatus.eq("COMPLETED")));
            default -> null;
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
