package com.ryuqq.marketplace.adapter.out.persistence.order.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.order.entity.QOrderItemHistoryJpaEntity.orderItemHistoryJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.order.entity.QOrderItemJpaEntity.orderItemJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.order.entity.QOrderJpaEntity.orderJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.order.entity.QPaymentJpaEntity.paymentJpaEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.order.condition.OrderConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderItemHistoryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderItemJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.PaymentJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * Order QueryDSL 레포지토리.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Repository
public class OrderQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final OrderConditionBuilder conditionBuilder;

    public OrderQueryDslRepository(
            JPAQueryFactory queryFactory, OrderConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    public Optional<OrderJpaEntity> findById(String orderId) {
        OrderJpaEntity entity =
                queryFactory
                        .selectFrom(orderJpaEntity)
                        .where(conditionBuilder.idEq(orderId), conditionBuilder.notDeleted())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    public Optional<OrderJpaEntity> findByOrderNumber(String orderNumber) {
        OrderJpaEntity entity =
                queryFactory
                        .selectFrom(orderJpaEntity)
                        .where(
                                conditionBuilder.orderNumberEq(orderNumber),
                                conditionBuilder.notDeleted())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    public boolean existsByExternalOrderNo(long salesChannelId, String externalOrderNo) {
        Integer fetchOne =
                queryFactory
                        .selectOne()
                        .from(orderJpaEntity)
                        .where(
                                conditionBuilder.salesChannelIdEq(salesChannelId),
                                conditionBuilder.externalOrderNoEq(externalOrderNo),
                                conditionBuilder.notDeleted())
                        .fetchFirst();
        return fetchOne != null;
    }

    public Optional<PaymentJpaEntity> findPaymentByOrderId(String orderId) {
        PaymentJpaEntity entity =
                queryFactory
                        .selectFrom(paymentJpaEntity)
                        .where(paymentJpaEntity.orderId.eq(orderId))
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    public List<OrderItemJpaEntity> findItemsByOrderId(String orderId) {
        return queryFactory
                .selectFrom(orderItemJpaEntity)
                .where(orderItemJpaEntity.orderId.eq(orderId))
                .fetch();
    }

    /** 주문상품 ID 목록으로 OrderItemHistory 일괄 조회. */
    public List<OrderItemHistoryJpaEntity> findItemHistoriesByOrderItemIds(
            List<String> orderItemIds) {
        if (orderItemIds == null || orderItemIds.isEmpty()) {
            return List.of();
        }
        return queryFactory
                .selectFrom(orderItemHistoryJpaEntity)
                .where(orderItemHistoryJpaEntity.orderItemId.in(orderItemIds))
                .orderBy(orderItemHistoryJpaEntity.changedAt.desc())
                .fetch();
    }
}
