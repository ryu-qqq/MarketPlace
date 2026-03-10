package com.ryuqq.marketplace.adapter.out.persistence.order.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.order.entity.QOrderHistoryJpaEntity.orderHistoryJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.order.entity.QOrderItemJpaEntity.orderItemJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.order.entity.QOrderJpaEntity.orderJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.order.entity.QPaymentJpaEntity.paymentJpaEntity;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.order.condition.OrderConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderHistoryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderItemJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.PaymentJpaEntity;
import com.ryuqq.marketplace.domain.order.vo.OrderStatus;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
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

    public List<OrderHistoryJpaEntity> findHistoriesByOrderId(String orderId) {
        return queryFactory
                .selectFrom(orderHistoryJpaEntity)
                .where(orderHistoryJpaEntity.orderId.eq(orderId))
                .orderBy(orderHistoryJpaEntity.changedAt.desc())
                .fetch();
    }

    /** 상태별 주문 건수 조회. */
    public Map<OrderStatus, Long> countByStatus() {
        List<Tuple> results =
                queryFactory
                        .select(orderJpaEntity.status, orderJpaEntity.count())
                        .from(orderJpaEntity)
                        .where(conditionBuilder.notDeleted())
                        .groupBy(orderJpaEntity.status)
                        .fetch();

        Map<OrderStatus, Long> statusCounts = new EnumMap<>(OrderStatus.class);
        for (Tuple tuple : results) {
            String statusName = tuple.get(orderJpaEntity.status);
            Long count = tuple.get(orderJpaEntity.count());
            if (statusName != null && count != null) {
                try {
                    statusCounts.put(OrderStatus.valueOf(statusName), count);
                } catch (IllegalArgumentException ignored) {
                    // 알 수 없는 status 값은 무시
                }
            }
        }
        return statusCounts;
    }
}
