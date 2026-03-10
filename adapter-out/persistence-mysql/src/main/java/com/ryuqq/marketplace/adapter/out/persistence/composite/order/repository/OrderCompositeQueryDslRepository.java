package com.ryuqq.marketplace.adapter.out.persistence.composite.order.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.order.entity.QOrderCancelJpaEntity.orderCancelJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.order.entity.QOrderClaimJpaEntity.orderClaimJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.order.entity.QOrderHistoryJpaEntity.orderHistoryJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.order.entity.QOrderItemJpaEntity.orderItemJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.order.entity.QOrderJpaEntity.orderJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.order.entity.QPaymentJpaEntity.paymentJpaEntity;

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
import com.ryuqq.marketplace.domain.order.query.OrderSearchCriteria;
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
                                orderItemJpaEntity.productId,
                                orderItemJpaEntity.sellerId,
                                orderItemJpaEntity.brandId,
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
                                orderItemJpaEntity.deliveryStatus,
                                orderItemJpaEntity.shipmentCompanyCode,
                                orderItemJpaEntity.invoice,
                                orderItemJpaEntity.shipmentCompletedDate,
                                orderItemJpaEntity.commissionRate,
                                orderItemJpaEntity.fee,
                                orderItemJpaEntity.expectationSettlementAmount,
                                orderItemJpaEntity.settlementAmount,
                                orderItemJpaEntity.shareRatio,
                                orderItemJpaEntity.expectedSettlementDay,
                                orderItemJpaEntity.settlementDay))
                .from(orderItemJpaEntity)
                .where(conditionBuilder.itemOrderIdEq(orderId))
                .fetch();
    }

    /** 주문 히스토리 목록 조회. */
    public List<OrderHistoryProjectionDto> findOrderHistories(String orderId) {
        return queryFactory
                .select(
                        Projections.constructor(
                                OrderHistoryProjectionDto.class,
                                orderHistoryJpaEntity.id,
                                orderHistoryJpaEntity.fromStatus,
                                orderHistoryJpaEntity.toStatus,
                                orderHistoryJpaEntity.changedBy,
                                orderHistoryJpaEntity.reason,
                                orderHistoryJpaEntity.changedAt))
                .from(orderHistoryJpaEntity)
                .where(conditionBuilder.historyOrderIdEq(orderId))
                .orderBy(orderHistoryJpaEntity.changedAt.desc())
                .fetch();
    }

    /** 주문 취소 목록 조회. */
    public List<OrderCancelProjectionDto> findOrderCancels(String orderId) {
        return queryFactory
                .select(
                        Projections.constructor(
                                OrderCancelProjectionDto.class,
                                orderCancelJpaEntity.id,
                                orderCancelJpaEntity.orderItemId,
                                orderCancelJpaEntity.cancelNumber,
                                orderCancelJpaEntity.cancelStatus,
                                orderCancelJpaEntity.quantity,
                                orderCancelJpaEntity.reasonType,
                                orderCancelJpaEntity.reasonDetail,
                                orderCancelJpaEntity.originalAmount,
                                orderCancelJpaEntity.refundAmount,
                                orderCancelJpaEntity.refundMethod,
                                orderCancelJpaEntity.refundedAt,
                                orderCancelJpaEntity.requestedAt,
                                orderCancelJpaEntity.completedAt))
                .from(orderCancelJpaEntity)
                .where(conditionBuilder.cancelOrderIdEq(orderId))
                .orderBy(orderCancelJpaEntity.requestedAt.desc())
                .fetch();
    }

    /** 주문 클레임 목록 조회. */
    public List<OrderClaimProjectionDto> findOrderClaims(String orderId) {
        return queryFactory
                .select(
                        Projections.constructor(
                                OrderClaimProjectionDto.class,
                                orderClaimJpaEntity.id,
                                orderClaimJpaEntity.orderItemId,
                                orderClaimJpaEntity.claimNumber,
                                orderClaimJpaEntity.claimType,
                                orderClaimJpaEntity.claimStatus,
                                orderClaimJpaEntity.quantity,
                                orderClaimJpaEntity.reasonType,
                                orderClaimJpaEntity.reasonDetail,
                                orderClaimJpaEntity.collectMethod,
                                orderClaimJpaEntity.originalAmount,
                                orderClaimJpaEntity.deductionAmount,
                                orderClaimJpaEntity.deductionReason,
                                orderClaimJpaEntity.refundAmount,
                                orderClaimJpaEntity.refundMethod,
                                orderClaimJpaEntity.refundedAt,
                                orderClaimJpaEntity.requestedAt,
                                orderClaimJpaEntity.completedAt,
                                orderClaimJpaEntity.rejectedAt))
                .from(orderClaimJpaEntity)
                .where(conditionBuilder.claimOrderIdEq(orderId))
                .orderBy(orderClaimJpaEntity.requestedAt.desc())
                .fetch();
    }
}
