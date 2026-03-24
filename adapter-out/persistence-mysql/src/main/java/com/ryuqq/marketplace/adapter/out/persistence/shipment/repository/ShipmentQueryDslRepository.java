package com.ryuqq.marketplace.adapter.out.persistence.shipment.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.order.entity.QOrderItemJpaEntity.orderItemJpaEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.order.entity.QOrderJpaEntity.orderJpaEntity;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.shipment.condition.ShipmentConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.shipment.entity.QShipmentJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.shipment.entity.ShipmentJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.shipment.query.ShipmentSearchCriteria;
import com.ryuqq.marketplace.domain.shipment.query.ShipmentSortKey;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentStatus;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** Shipment QueryDSL Repository. */
@Repository
public class ShipmentQueryDslRepository {

    private static final QShipmentJpaEntity shipment = QShipmentJpaEntity.shipmentJpaEntity;

    private final JPAQueryFactory queryFactory;
    private final ShipmentConditionBuilder conditionBuilder;

    public ShipmentQueryDslRepository(
            JPAQueryFactory queryFactory, ShipmentConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    public Optional<ShipmentJpaEntity> findById(String id) {
        ShipmentJpaEntity entity =
                queryFactory
                        .selectFrom(shipment)
                        .where(conditionBuilder.idEq(id), conditionBuilder.notDeleted())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    public Optional<ShipmentJpaEntity> findByOrderItemId(String orderItemId) {
        ShipmentJpaEntity entity =
                queryFactory
                        .selectFrom(shipment)
                        .where(
                                conditionBuilder.orderItemIdEq(orderItemId),
                                conditionBuilder.notDeleted())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    public List<ShipmentJpaEntity> findByOrderItemIds(List<String> orderItemIds) {
        return queryFactory
                .selectFrom(shipment)
                .where(conditionBuilder.orderItemIdIn(orderItemIds), conditionBuilder.notDeleted())
                .fetch();
    }

    public List<ShipmentJpaEntity> findByCriteria(ShipmentSearchCriteria criteria) {
        return queryFactory
                .selectFrom(shipment)
                .where(
                        conditionBuilder.statusIn(criteria),
                        conditionBuilder.sellerIdsIn(criteria),
                        conditionBuilder.shopOrderNosIn(criteria),
                        conditionBuilder.searchCondition(criteria),
                        conditionBuilder.dateRange(criteria),
                        conditionBuilder.notDeleted())
                .orderBy(resolveOrderSpecifier(criteria))
                .offset(criteria.offset())
                .limit(criteria.size())
                .fetch();
    }

    public long countByCriteria(ShipmentSearchCriteria criteria) {
        Long count =
                queryFactory
                        .select(shipment.count())
                        .from(shipment)
                        .where(
                                conditionBuilder.statusIn(criteria),
                                conditionBuilder.sellerIdsIn(criteria),
                                conditionBuilder.shopOrderNosIn(criteria),
                                conditionBuilder.searchCondition(criteria),
                                conditionBuilder.dateRange(criteria),
                                conditionBuilder.notDeleted())
                        .fetchOne();
        return count != null ? count : 0L;
    }

    public Map<String, Long> countByStatus() {
        List<Tuple> results =
                queryFactory
                        .select(shipment.status, shipment.count())
                        .from(shipment)
                        .where(conditionBuilder.notDeleted())
                        .groupBy(shipment.status)
                        .fetch();

        Map<String, Long> statusCounts = new HashMap<>();
        for (Tuple tuple : results) {
            String status = tuple.get(shipment.status);
            Long count = tuple.get(shipment.count());
            if (status != null && count != null) {
                statusCounts.put(status, count);
            }
        }
        return statusCounts;
    }

    // ===== OrderItem 기준 Fulfillment 조회 (shipment 없는 주문도 포함) =====

    /**
     * OrderItem 기준 주문 이행 목록 조회.
     *
     * <p>order_items LEFT JOIN shipments — shipment 미생성 주문도 포함. READY 필터 시 shipment 없는
     * 주문도 포함합니다.
     *
     * @return orderItemId 목록 (페이징 적용)
     */
    public List<String> findFulfillmentOrderItemIds(ShipmentSearchCriteria criteria) {
        return queryFactory
                .select(orderItemJpaEntity.id)
                .from(orderItemJpaEntity)
                .leftJoin(shipment)
                .on(
                        shipment.orderItemId.eq(orderItemJpaEntity.id),
                        shipment.deletedAt.isNull())
                .join(orderJpaEntity)
                .on(orderItemJpaEntity.orderId.eq(orderJpaEntity.id))
                .where(
                        fulfillmentStatusFilter(criteria),
                        fulfillmentSearchCondition(criteria),
                        fulfillmentDateRange(criteria),
                        fulfillmentSellerFilter(criteria),
                        fulfillmentShopOrderNoFilter(criteria))
                .orderBy(orderItemJpaEntity.createdAt.desc())
                .offset(criteria.offset())
                .limit(criteria.size())
                .fetch();
    }

    /** OrderItem 기준 주문 이행 목록 카운트. */
    public long countFulfillment(ShipmentSearchCriteria criteria) {
        Long count =
                queryFactory
                        .select(orderItemJpaEntity.count())
                        .from(orderItemJpaEntity)
                        .leftJoin(shipment)
                        .on(
                                shipment.orderItemId.eq(orderItemJpaEntity.id),
                                shipment.deletedAt.isNull())
                        .join(orderJpaEntity)
                        .on(orderItemJpaEntity.orderId.eq(orderJpaEntity.id))
                        .where(
                                fulfillmentStatusFilter(criteria),
                                fulfillmentSearchCondition(criteria),
                                fulfillmentDateRange(criteria),
                                fulfillmentSellerFilter(criteria),
                                fulfillmentShopOrderNoFilter(criteria))
                        .fetchOne();
        return count != null ? count : 0L;
    }

    /**
     * 상태 필터. READY 선택 시 shipment 없는 주문(신규)도 포함.
     *
     * <p>- READY → (shipment IS NULL) OR (shipment.status = 'READY')
     *
     * <p>- 그 외 → shipment.status IN (...)
     */
    private BooleanExpression fulfillmentStatusFilter(ShipmentSearchCriteria criteria) {
        if (!criteria.hasStatusFilter()) {
            return null;
        }
        List<String> statusNames = criteria.statuses().stream().map(ShipmentStatus::name).toList();
        boolean includesReady = statusNames.contains("READY");

        if (includesReady) {
            BooleanExpression noShipment = shipment.id.isNull();
            BooleanExpression shipmentReady = shipment.status.eq("READY");
            BooleanExpression readyCondition = noShipment.or(shipmentReady);

            List<String> otherStatuses =
                    statusNames.stream().filter(s -> !"READY".equals(s)).toList();
            if (otherStatuses.isEmpty()) {
                return readyCondition;
            }
            return readyCondition.or(shipment.status.in(otherStatuses));
        }

        return shipment.status.in(statusNames);
    }

    private BooleanExpression fulfillmentSearchCondition(ShipmentSearchCriteria criteria) {
        if (!criteria.hasSearchCondition()) {
            return null;
        }
        String word = "%" + criteria.searchWord() + "%";
        if (!criteria.hasSearchField()) {
            return orderItemJpaEntity.externalProductName.like(word);
        }
        return switch (criteria.searchField()) {
            case ORDER_ID -> orderItemJpaEntity.id.eq(criteria.searchWord().trim());
            case TRACKING_NUMBER -> shipment.trackingNumber.like(word);
            case CUSTOMER_NAME -> orderJpaEntity.buyerName.like(word);
            case CUSTOMER_PHONE -> orderJpaEntity.buyerPhone.like(word);
            case PRODUCT_NAME -> orderItemJpaEntity.externalProductName.like(word);
            case SHOP_ORDER_NO -> orderJpaEntity.externalOrderNo.like(word);
        };
    }

    private BooleanExpression fulfillmentDateRange(ShipmentSearchCriteria criteria) {
        if (criteria.dateRange() == null || criteria.dateRange().isEmpty()) {
            return null;
        }
        // shipment가 없는 주문도 포함하므로 order_items.created_at 기본 사용
        var path = orderItemJpaEntity.createdAt;

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

    private BooleanExpression fulfillmentSellerFilter(ShipmentSearchCriteria criteria) {
        if (!criteria.hasSellerFilter()) {
            return null;
        }
        return orderJpaEntity.shopId.in(criteria.sellerIds());
    }

    private BooleanExpression fulfillmentShopOrderNoFilter(ShipmentSearchCriteria criteria) {
        if (!criteria.hasShopOrderNoFilter()) {
            return null;
        }
        return orderJpaEntity.externalOrderNo.in(criteria.shopOrderNos());
    }

    private OrderSpecifier<?> resolveOrderSpecifier(ShipmentSearchCriteria criteria) {
        ShipmentSortKey sortKey = criteria.queryContext().sortKey();
        SortDirection direction = criteria.queryContext().sortDirection();
        boolean isAsc = direction == SortDirection.ASC;

        return switch (sortKey) {
            case CREATED_AT -> isAsc ? shipment.createdAt.asc() : shipment.createdAt.desc();
            case SHIPPED_AT -> isAsc ? shipment.shippedAt.asc() : shipment.shippedAt.desc();
            case DELIVERED_AT -> isAsc ? shipment.deliveredAt.asc() : shipment.deliveredAt.desc();
        };
    }
}
