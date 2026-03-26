package com.ryuqq.marketplace.adapter.out.persistence.composite.order.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.cancel.entity.QCancelJpaEntity.cancelJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.exchange.entity.QExchangeClaimJpaEntity.exchangeClaimJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.order.entity.QOrderItemHistoryJpaEntity.orderItemHistoryJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.order.entity.QOrderItemJpaEntity.orderItemJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.order.entity.QOrderJpaEntity.orderJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.order.entity.QPaymentJpaEntity.paymentJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.refund.entity.QRefundClaimJpaEntity.refundClaimJpaEntity;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.composite.order.condition.OrderCompositeConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.composite.order.dto.OrderCancelProjectionDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.order.dto.OrderClaimProjectionDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.order.dto.OrderHistoryProjectionDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.order.dto.OrderItemProjectionDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.order.dto.OrderListProjectionDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.order.dto.PaymentProjectionDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.order.dto.ProductOrderDetailProjectionDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.order.dto.ProductOrderListProjectionDto;
import com.ryuqq.marketplace.domain.order.query.OrderSearchCriteria;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** Order Composite QueryDSL Repository. 목록/상세 조회를 위한 다중 쿼리 조합. */
@Repository
public class OrderCompositeQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final OrderCompositeConditionBuilder conditionBuilder;

    public OrderCompositeQueryDslRepository(
            JPAQueryFactory queryFactory, OrderCompositeConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    // ===== 상품주문(productOrder) 기반 목록 조회 =====

    /** 상품주문 목록 조회 (order_items JOIN orders LEFT JOIN payments). 각 행 = 1 order_item. */
    public List<ProductOrderListProjectionDto> searchProductOrders(OrderSearchCriteria criteria) {
        return queryFactory
                .select(
                        Projections.constructor(
                                ProductOrderListProjectionDto.class,
                                // orders
                                orderJpaEntity.id,
                                orderJpaEntity.orderNumber,
                                orderJpaEntity.status,
                                orderJpaEntity.salesChannelId,
                                orderJpaEntity.shopId,
                                orderJpaEntity.shopCode,
                                orderJpaEntity.shopName,
                                orderJpaEntity.externalOrderNo,
                                orderJpaEntity.externalOrderedAt,
                                orderJpaEntity.buyerName,
                                orderJpaEntity.buyerEmail,
                                orderJpaEntity.buyerPhone,
                                orderJpaEntity.createdAt,
                                orderJpaEntity.updatedAt,
                                // order_items
                                orderItemJpaEntity.id,
                                orderItemJpaEntity.orderItemNumber,
                                orderItemJpaEntity.productGroupId,
                                orderItemJpaEntity.sellerId,
                                orderItemJpaEntity.brandId,
                                orderItemJpaEntity.productId,
                                orderItemJpaEntity.skuCode,
                                orderItemJpaEntity.productGroupName,
                                orderItemJpaEntity.brandName,
                                orderItemJpaEntity.sellerName,
                                orderItemJpaEntity.mainImageUrl,
                                orderItemJpaEntity.externalProductId,
                                orderItemJpaEntity.externalOptionId,
                                orderItemJpaEntity.externalProductName,
                                orderItemJpaEntity.externalOptionName,
                                orderItemJpaEntity.externalImageUrl,
                                orderItemJpaEntity.unitPrice,
                                orderItemJpaEntity.quantity,
                                orderItemJpaEntity.totalAmount,
                                orderItemJpaEntity.discountAmount,
                                orderItemJpaEntity.paymentAmount,
                                orderItemJpaEntity.receiverName,
                                orderItemJpaEntity.receiverPhone,
                                orderItemJpaEntity.receiverZipcode,
                                orderItemJpaEntity.receiverAddress,
                                orderItemJpaEntity.receiverAddressDetail,
                                orderItemJpaEntity.deliveryRequest,
                                orderItemJpaEntity.orderItemStatus,
                                orderItemJpaEntity.externalOrderStatus,
                                // payments (LEFT JOIN)
                                paymentJpaEntity.id,
                                paymentJpaEntity.paymentNumber,
                                paymentJpaEntity.paymentStatus,
                                paymentJpaEntity.paymentMethod,
                                paymentJpaEntity.paymentAgencyId,
                                paymentJpaEntity.paymentAmount,
                                paymentJpaEntity.paidAt,
                                paymentJpaEntity.canceledAt))
                .from(orderItemJpaEntity)
                .join(orderJpaEntity)
                .on(conditionBuilder.itemOrderJoinCondition())
                .leftJoin(paymentJpaEntity)
                .on(conditionBuilder.paymentJoinCondition())
                .where(conditionBuilder.buildWhereConditions(criteria))
                .orderBy(conditionBuilder.buildOrderSpecifier(criteria))
                .offset(criteria.offset())
                .limit(criteria.size())
                .fetch();
    }

    /** 상품주문 목록 카운트 (order_items 기준). */
    public long countProductOrders(OrderSearchCriteria criteria) {
        Long count =
                queryFactory
                        .select(orderItemJpaEntity.count())
                        .from(orderItemJpaEntity)
                        .join(orderJpaEntity)
                        .on(conditionBuilder.itemOrderJoinCondition())
                        .where(conditionBuilder.buildWhereConditions(criteria))
                        .fetchOne();
        return count != null ? count : 0L;
    }

    // ===== 상품주문(productOrder) 상세 조회 =====

    /** 상품주문 상세 단건 조회 (order_items JOIN orders LEFT JOIN payments + 정산 필드). */
    public Optional<ProductOrderDetailProjectionDto> findProductOrderDetail(String orderItemId) {
        ProductOrderDetailProjectionDto result =
                queryFactory
                        .select(
                                Projections.constructor(
                                        ProductOrderDetailProjectionDto.class,
                                        // orders
                                        orderJpaEntity.id,
                                        orderJpaEntity.orderNumber,
                                        orderJpaEntity.status,
                                        orderJpaEntity.salesChannelId,
                                        orderJpaEntity.shopId,
                                        orderJpaEntity.shopCode,
                                        orderJpaEntity.shopName,
                                        orderJpaEntity.externalOrderNo,
                                        orderJpaEntity.externalOrderedAt,
                                        orderJpaEntity.buyerName,
                                        orderJpaEntity.buyerEmail,
                                        orderJpaEntity.buyerPhone,
                                        orderJpaEntity.createdAt,
                                        orderJpaEntity.updatedAt,
                                        // order_items
                                        orderItemJpaEntity.id,
                                        orderItemJpaEntity.orderItemNumber,
                                        orderItemJpaEntity.productGroupId,
                                        orderItemJpaEntity.sellerId,
                                        orderItemJpaEntity.brandId,
                                        orderItemJpaEntity.productId,
                                        orderItemJpaEntity.skuCode,
                                        orderItemJpaEntity.productGroupName,
                                        orderItemJpaEntity.brandName,
                                        orderItemJpaEntity.sellerName,
                                        orderItemJpaEntity.mainImageUrl,
                                        orderItemJpaEntity.externalProductId,
                                        orderItemJpaEntity.externalOptionId,
                                        orderItemJpaEntity.externalProductName,
                                        orderItemJpaEntity.externalOptionName,
                                        orderItemJpaEntity.externalImageUrl,
                                        orderItemJpaEntity.unitPrice,
                                        orderItemJpaEntity.quantity,
                                        orderItemJpaEntity.totalAmount,
                                        orderItemJpaEntity.discountAmount,
                                        orderItemJpaEntity.paymentAmount,
                                        orderItemJpaEntity.receiverName,
                                        orderItemJpaEntity.receiverPhone,
                                        orderItemJpaEntity.receiverZipcode,
                                        orderItemJpaEntity.receiverAddress,
                                        orderItemJpaEntity.receiverAddressDetail,
                                        orderItemJpaEntity.deliveryRequest,
                                        orderItemJpaEntity.orderItemStatus,
                                        orderItemJpaEntity.externalOrderStatus,
                                        // payments (LEFT JOIN)
                                        paymentJpaEntity.id,
                                        paymentJpaEntity.paymentNumber,
                                        paymentJpaEntity.paymentStatus,
                                        paymentJpaEntity.paymentMethod,
                                        paymentJpaEntity.paymentAgencyId,
                                        paymentJpaEntity.paymentAmount,
                                        paymentJpaEntity.paidAt,
                                        paymentJpaEntity.canceledAt))
                        .from(orderItemJpaEntity)
                        .join(orderJpaEntity)
                        .on(conditionBuilder.itemOrderJoinCondition())
                        .leftJoin(paymentJpaEntity)
                        .on(conditionBuilder.paymentJoinCondition())
                        .where(
                                orderItemJpaEntity.id.eq(orderItemId),
                                conditionBuilder.orderNotDeleted())
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /** 상품주문 단건 취소 목록 조회. */
    public List<OrderCancelProjectionDto> findCancelsByOrderItemId(String orderItemId) {
        return findCancelsByCondition(cancelJpaEntity.orderItemId.eq(orderItemId));
    }

    /** 상품주문 단건 클레임 목록 조회 (refund_claims + exchange_claims UNION). */
    public List<OrderClaimProjectionDto> findClaimsByOrderItemId(String orderItemId) {
        List<OrderClaimProjectionDto> refunds =
                findRefundClaimsByCondition(refundClaimJpaEntity.orderItemId.eq(orderItemId));
        List<OrderClaimProjectionDto> exchanges =
                findExchangeClaimsByCondition(exchangeClaimJpaEntity.orderItemId.eq(orderItemId));
        return mergeAndSortClaims(refunds, exchanges);
    }

    /** 주문상품 ID 목록 기반 취소 일괄 조회. */
    public List<OrderCancelProjectionDto> findCancelsByOrderItemIds(List<String> orderItemIds) {
        return findCancelsByCondition(cancelJpaEntity.orderItemId.in(orderItemIds));
    }

    /** 주문상품 ID 목록 기반 클레임 일괄 조회 (refund_claims + exchange_claims UNION). */
    public List<OrderClaimProjectionDto> findClaimsByOrderItemIds(List<String> orderItemIds) {
        List<OrderClaimProjectionDto> refunds =
                findRefundClaimsByCondition(refundClaimJpaEntity.orderItemId.in(orderItemIds));
        List<OrderClaimProjectionDto> exchanges =
                findExchangeClaimsByCondition(exchangeClaimJpaEntity.orderItemId.in(orderItemIds));
        return mergeAndSortClaims(refunds, exchanges);
    }

    private List<OrderCancelProjectionDto> findCancelsByCondition(
            com.querydsl.core.types.Predicate condition) {
        return queryFactory
                .select(
                        Projections.constructor(
                                OrderCancelProjectionDto.class,
                                cancelJpaEntity.id,
                                cancelJpaEntity.orderItemId,
                                cancelJpaEntity.cancelNumber,
                                cancelJpaEntity.cancelStatus,
                                cancelJpaEntity.cancelQty,
                                cancelJpaEntity.reasonType,
                                cancelJpaEntity.reasonDetail,
                                cancelJpaEntity.refundAmount,
                                cancelJpaEntity.refundMethod,
                                cancelJpaEntity.refundedAt,
                                cancelJpaEntity.requestedAt,
                                cancelJpaEntity.completedAt))
                .from(cancelJpaEntity)
                .where(condition)
                .orderBy(cancelJpaEntity.requestedAt.desc())
                .fetch();
    }

    private List<OrderClaimProjectionDto> findRefundClaimsByCondition(
            com.querydsl.core.types.Predicate condition) {
        return queryFactory
                .select(
                        Projections.constructor(
                                OrderClaimProjectionDto.class,
                                refundClaimJpaEntity.id,
                                refundClaimJpaEntity.orderItemId,
                                refundClaimJpaEntity.claimNumber,
                                com.querydsl.core.types.dsl.Expressions.constant("REFUND"),
                                refundClaimJpaEntity.refundStatus,
                                refundClaimJpaEntity.refundQty,
                                refundClaimJpaEntity.reasonType,
                                refundClaimJpaEntity.reasonDetail,
                                com.querydsl.core.types.dsl.Expressions.nullExpression(
                                        String.class),
                                refundClaimJpaEntity.originalAmount,
                                refundClaimJpaEntity.deductionAmount,
                                refundClaimJpaEntity.deductionReason,
                                refundClaimJpaEntity.finalAmount,
                                refundClaimJpaEntity.refundMethod,
                                refundClaimJpaEntity.refundedAt,
                                refundClaimJpaEntity.requestedAt,
                                refundClaimJpaEntity.completedAt,
                                com.querydsl.core.types.dsl.Expressions.nullExpression(
                                        Instant.class)))
                .from(refundClaimJpaEntity)
                .where(condition)
                .orderBy(refundClaimJpaEntity.requestedAt.desc())
                .fetch();
    }

    private List<OrderClaimProjectionDto> findExchangeClaimsByCondition(
            com.querydsl.core.types.Predicate condition) {
        return queryFactory
                .select(
                        Projections.constructor(
                                OrderClaimProjectionDto.class,
                                exchangeClaimJpaEntity.id,
                                exchangeClaimJpaEntity.orderItemId,
                                exchangeClaimJpaEntity.claimNumber,
                                com.querydsl.core.types.dsl.Expressions.constant("EXCHANGE"),
                                exchangeClaimJpaEntity.exchangeStatus,
                                exchangeClaimJpaEntity.exchangeQty,
                                exchangeClaimJpaEntity.reasonType,
                                exchangeClaimJpaEntity.reasonDetail,
                                com.querydsl.core.types.dsl.Expressions.nullExpression(
                                        String.class),
                                com.querydsl.core.types.dsl.Expressions.nullExpression(
                                        Integer.class),
                                com.querydsl.core.types.dsl.Expressions.nullExpression(
                                        Integer.class),
                                com.querydsl.core.types.dsl.Expressions.nullExpression(
                                        String.class),
                                com.querydsl.core.types.dsl.Expressions.nullExpression(
                                        Integer.class),
                                com.querydsl.core.types.dsl.Expressions.nullExpression(
                                        String.class),
                                com.querydsl.core.types.dsl.Expressions.nullExpression(
                                        Instant.class),
                                exchangeClaimJpaEntity.requestedAt,
                                exchangeClaimJpaEntity.completedAt,
                                com.querydsl.core.types.dsl.Expressions.nullExpression(
                                        Instant.class)))
                .from(exchangeClaimJpaEntity)
                .where(condition)
                .orderBy(exchangeClaimJpaEntity.requestedAt.desc())
                .fetch();
    }

    private List<OrderClaimProjectionDto> mergeAndSortClaims(
            List<OrderClaimProjectionDto> refunds, List<OrderClaimProjectionDto> exchanges) {
        List<OrderClaimProjectionDto> merged =
                new java.util.ArrayList<>(refunds.size() + exchanges.size());
        merged.addAll(refunds);
        merged.addAll(exchanges);
        merged.sort(
                java.util.Comparator.comparing(
                        OrderClaimProjectionDto::requestedAt,
                        java.util.Comparator.nullsLast(java.util.Comparator.reverseOrder())));
        return merged;
    }

    /** 주문 ID 목록으로 주문 기본정보 일괄 조회 (orders + payments LEFT JOIN). */
    public List<OrderListProjectionDto> findOrdersByIds(List<String> orderIds) {
        return queryFactory
                .select(
                        Projections.constructor(
                                OrderListProjectionDto.class,
                                orderJpaEntity.id,
                                orderJpaEntity.orderNumber,
                                orderJpaEntity.status,
                                orderJpaEntity.salesChannelId,
                                orderJpaEntity.shopId,
                                orderJpaEntity.shopCode,
                                orderJpaEntity.shopName,
                                orderJpaEntity.externalOrderNo,
                                orderJpaEntity.externalOrderedAt,
                                orderJpaEntity.buyerName,
                                orderJpaEntity.buyerEmail,
                                orderJpaEntity.buyerPhone,
                                orderJpaEntity.createdAt,
                                orderJpaEntity.updatedAt,
                                paymentJpaEntity.id,
                                paymentJpaEntity.paymentNumber,
                                paymentJpaEntity.paymentStatus,
                                paymentJpaEntity.paymentMethod,
                                paymentJpaEntity.paymentAmount,
                                paymentJpaEntity.paidAt,
                                JPAExpressions.select(orderItemJpaEntity.count())
                                        .from(orderItemJpaEntity)
                                        .where(orderItemJpaEntity.orderId.eq(orderJpaEntity.id))))
                .from(orderJpaEntity)
                .leftJoin(paymentJpaEntity)
                .on(conditionBuilder.paymentJoinCondition())
                .where(orderJpaEntity.id.in(orderIds), conditionBuilder.orderNotDeleted())
                .fetch();
    }

    /** 주문상품 ID 목록으로 상품주문 일괄 조회 (order_items JOIN orders LEFT JOIN payments). */
    public List<ProductOrderListProjectionDto> findOrderItemsByIds(List<String> orderItemIds) {
        return queryFactory
                .select(
                        Projections.constructor(
                                ProductOrderListProjectionDto.class,
                                // orders
                                orderJpaEntity.id,
                                orderJpaEntity.orderNumber,
                                orderJpaEntity.status,
                                orderJpaEntity.salesChannelId,
                                orderJpaEntity.shopId,
                                orderJpaEntity.shopCode,
                                orderJpaEntity.shopName,
                                orderJpaEntity.externalOrderNo,
                                orderJpaEntity.externalOrderedAt,
                                orderJpaEntity.buyerName,
                                orderJpaEntity.buyerEmail,
                                orderJpaEntity.buyerPhone,
                                orderJpaEntity.createdAt,
                                orderJpaEntity.updatedAt,
                                // order_items
                                orderItemJpaEntity.id,
                                orderItemJpaEntity.orderItemNumber,
                                orderItemJpaEntity.productGroupId,
                                orderItemJpaEntity.sellerId,
                                orderItemJpaEntity.brandId,
                                orderItemJpaEntity.productId,
                                orderItemJpaEntity.skuCode,
                                orderItemJpaEntity.productGroupName,
                                orderItemJpaEntity.brandName,
                                orderItemJpaEntity.sellerName,
                                orderItemJpaEntity.mainImageUrl,
                                orderItemJpaEntity.externalProductId,
                                orderItemJpaEntity.externalOptionId,
                                orderItemJpaEntity.externalProductName,
                                orderItemJpaEntity.externalOptionName,
                                orderItemJpaEntity.externalImageUrl,
                                orderItemJpaEntity.unitPrice,
                                orderItemJpaEntity.quantity,
                                orderItemJpaEntity.totalAmount,
                                orderItemJpaEntity.discountAmount,
                                orderItemJpaEntity.paymentAmount,
                                orderItemJpaEntity.receiverName,
                                orderItemJpaEntity.receiverPhone,
                                orderItemJpaEntity.receiverZipcode,
                                orderItemJpaEntity.receiverAddress,
                                orderItemJpaEntity.receiverAddressDetail,
                                orderItemJpaEntity.deliveryRequest,
                                orderItemJpaEntity.orderItemStatus,
                                orderItemJpaEntity.externalOrderStatus,
                                // payments (LEFT JOIN)
                                paymentJpaEntity.id,
                                paymentJpaEntity.paymentNumber,
                                paymentJpaEntity.paymentStatus,
                                paymentJpaEntity.paymentMethod,
                                paymentJpaEntity.paymentAgencyId,
                                paymentJpaEntity.paymentAmount,
                                paymentJpaEntity.paidAt,
                                paymentJpaEntity.canceledAt))
                .from(orderItemJpaEntity)
                .join(orderJpaEntity)
                .on(conditionBuilder.itemOrderJoinCondition())
                .leftJoin(paymentJpaEntity)
                .on(conditionBuilder.paymentJoinCondition())
                .where(orderItemJpaEntity.id.in(orderItemIds), conditionBuilder.orderNotDeleted())
                .fetch();
    }

    // ===== 기존 주문(order) 기반 조회 =====

    /** 주문 목록 조회 (orders + payments LEFT JOIN + item count subquery). */
    public List<OrderListProjectionDto> searchOrders(OrderSearchCriteria criteria) {
        return queryFactory
                .select(
                        Projections.constructor(
                                OrderListProjectionDto.class,
                                orderJpaEntity.id,
                                orderJpaEntity.orderNumber,
                                orderJpaEntity.status,
                                orderJpaEntity.salesChannelId,
                                orderJpaEntity.shopId,
                                orderJpaEntity.shopCode,
                                orderJpaEntity.shopName,
                                orderJpaEntity.externalOrderNo,
                                orderJpaEntity.externalOrderedAt,
                                orderJpaEntity.buyerName,
                                orderJpaEntity.buyerEmail,
                                orderJpaEntity.buyerPhone,
                                orderJpaEntity.createdAt,
                                orderJpaEntity.updatedAt,
                                paymentJpaEntity.id,
                                paymentJpaEntity.paymentNumber,
                                paymentJpaEntity.paymentStatus,
                                paymentJpaEntity.paymentMethod,
                                paymentJpaEntity.paymentAmount,
                                paymentJpaEntity.paidAt,
                                JPAExpressions.select(orderItemJpaEntity.count())
                                        .from(orderItemJpaEntity)
                                        .where(orderItemJpaEntity.orderId.eq(orderJpaEntity.id))))
                .from(orderJpaEntity)
                .leftJoin(paymentJpaEntity)
                .on(conditionBuilder.paymentJoinCondition())
                .where(conditionBuilder.buildWhereConditions(criteria))
                .orderBy(conditionBuilder.buildOrderSpecifier(criteria))
                .offset(criteria.offset())
                .limit(criteria.size())
                .fetch();
    }

    /** 주문 목록 카운트. */
    public long countOrders(OrderSearchCriteria criteria) {
        Long count =
                queryFactory
                        .select(orderJpaEntity.count())
                        .from(orderJpaEntity)
                        .where(conditionBuilder.buildWhereConditions(criteria))
                        .fetchOne();
        return count != null ? count : 0L;
    }

    /** 주문 상세 - 주문 헤더 + 결제 정보 (1:1). */
    public Optional<OrderListProjectionDto> findOrderWithPayment(String orderId) {
        OrderListProjectionDto result =
                queryFactory
                        .select(
                                Projections.constructor(
                                        OrderListProjectionDto.class,
                                        orderJpaEntity.id,
                                        orderJpaEntity.orderNumber,
                                        orderJpaEntity.status,
                                        orderJpaEntity.salesChannelId,
                                        orderJpaEntity.shopId,
                                        orderJpaEntity.shopCode,
                                        orderJpaEntity.shopName,
                                        orderJpaEntity.externalOrderNo,
                                        orderJpaEntity.externalOrderedAt,
                                        orderJpaEntity.buyerName,
                                        orderJpaEntity.buyerEmail,
                                        orderJpaEntity.buyerPhone,
                                        orderJpaEntity.createdAt,
                                        orderJpaEntity.updatedAt,
                                        paymentJpaEntity.id,
                                        paymentJpaEntity.paymentNumber,
                                        paymentJpaEntity.paymentStatus,
                                        paymentJpaEntity.paymentMethod,
                                        paymentJpaEntity.paymentAmount,
                                        paymentJpaEntity.paidAt,
                                        JPAExpressions.select(orderItemJpaEntity.count())
                                                .from(orderItemJpaEntity)
                                                .where(
                                                        orderItemJpaEntity.orderId.eq(
                                                                orderJpaEntity.id))))
                        .from(orderJpaEntity)
                        .leftJoin(paymentJpaEntity)
                        .on(conditionBuilder.paymentJoinCondition())
                        .where(
                                conditionBuilder.orderIdEq(orderId),
                                conditionBuilder.orderNotDeleted())
                        .fetchOne();

        return Optional.ofNullable(result);
    }

    /** 주문 결제 정보 단건 조회. */
    public Optional<PaymentProjectionDto> findPayment(String orderId) {
        PaymentProjectionDto result =
                queryFactory
                        .select(
                                Projections.constructor(
                                        PaymentProjectionDto.class,
                                        paymentJpaEntity.id,
                                        paymentJpaEntity.paymentNumber,
                                        paymentJpaEntity.orderId,
                                        paymentJpaEntity.paymentStatus,
                                        paymentJpaEntity.paymentMethod,
                                        paymentJpaEntity.paymentAgencyId,
                                        paymentJpaEntity.paymentAmount,
                                        paymentJpaEntity.paidAt,
                                        paymentJpaEntity.canceledAt))
                        .from(paymentJpaEntity)
                        .where(paymentJpaEntity.orderId.eq(orderId))
                        .fetchOne();

        return Optional.ofNullable(result);
    }

    /** 주문 상품 목록 조회. */
    public List<OrderItemProjectionDto> findOrderItems(String orderId) {
        return queryFactory
                .select(
                        Projections.constructor(
                                OrderItemProjectionDto.class,
                                orderItemJpaEntity.id,
                                orderItemJpaEntity.orderId,
                                orderItemJpaEntity.productGroupId,
                                orderItemJpaEntity.sellerId,
                                orderItemJpaEntity.brandId,
                                orderItemJpaEntity.productId,
                                orderItemJpaEntity.skuCode,
                                orderItemJpaEntity.productGroupName,
                                orderItemJpaEntity.brandName,
                                orderItemJpaEntity.sellerName,
                                orderItemJpaEntity.mainImageUrl,
                                orderItemJpaEntity.externalProductId,
                                orderItemJpaEntity.externalOptionId,
                                orderItemJpaEntity.externalProductName,
                                orderItemJpaEntity.externalOptionName,
                                orderItemJpaEntity.externalImageUrl,
                                orderItemJpaEntity.unitPrice,
                                orderItemJpaEntity.quantity,
                                orderItemJpaEntity.totalAmount,
                                orderItemJpaEntity.discountAmount,
                                orderItemJpaEntity.paymentAmount,
                                orderItemJpaEntity.receiverName,
                                orderItemJpaEntity.receiverPhone,
                                orderItemJpaEntity.receiverZipcode,
                                orderItemJpaEntity.receiverAddress,
                                orderItemJpaEntity.receiverAddressDetail,
                                orderItemJpaEntity.deliveryRequest,
                                orderItemJpaEntity.orderItemStatus,
                                orderItemJpaEntity.externalOrderStatus))
                .from(orderItemJpaEntity)
                .where(conditionBuilder.itemOrderIdEq(orderId))
                .fetch();
    }

    /**
     * 주문 히스토리 목록 조회.
     *
     * <p>order_item_histories 테이블을 기준으로 조회합니다. orderId에 속한 order_items의 ID를 서브쿼리로 추출하여 histories를
     * 조회합니다.
     */
    public List<OrderHistoryProjectionDto> findOrderHistories(String orderId) {
        return queryFactory
                .select(
                        Projections.constructor(
                                OrderHistoryProjectionDto.class,
                                orderItemHistoryJpaEntity.id,
                                orderItemHistoryJpaEntity.fromStatus,
                                orderItemHistoryJpaEntity.toStatus,
                                orderItemHistoryJpaEntity.changedBy,
                                orderItemHistoryJpaEntity.reason,
                                orderItemHistoryJpaEntity.changedAt))
                .from(orderItemHistoryJpaEntity)
                .where(
                        orderItemHistoryJpaEntity.orderItemId.in(
                                JPAExpressions.select(orderItemJpaEntity.id)
                                        .from(orderItemJpaEntity)
                                        .where(orderItemJpaEntity.orderId.eq(orderId))))
                .orderBy(orderItemHistoryJpaEntity.changedAt.desc())
                .fetch();
    }

    /** 주문 취소 목록 조회 (orderId 기반 — order_items 서브쿼리). */
    public List<OrderCancelProjectionDto> findOrderCancels(String orderId) {
        return findCancelsByCondition(
                cancelJpaEntity.orderItemId.in(
                        JPAExpressions.select(orderItemJpaEntity.id)
                                .from(orderItemJpaEntity)
                                .where(orderItemJpaEntity.orderId.eq(orderId))));
    }

    /** 주문 클레임 목록 조회 (orderId 기반 — order_items 서브쿼리). */
    public List<OrderClaimProjectionDto> findOrderClaims(String orderId) {
        com.querydsl.core.types.dsl.StringPath subQuery = orderItemJpaEntity.id;
        var itemIds =
                JPAExpressions.select(subQuery)
                        .from(orderItemJpaEntity)
                        .where(orderItemJpaEntity.orderId.eq(orderId));
        List<OrderClaimProjectionDto> refunds =
                findRefundClaimsByCondition(refundClaimJpaEntity.orderItemId.in(itemIds));
        List<OrderClaimProjectionDto> exchanges =
                findExchangeClaimsByCondition(exchangeClaimJpaEntity.orderItemId.in(itemIds));
        return mergeAndSortClaims(refunds, exchanges);
    }
}
