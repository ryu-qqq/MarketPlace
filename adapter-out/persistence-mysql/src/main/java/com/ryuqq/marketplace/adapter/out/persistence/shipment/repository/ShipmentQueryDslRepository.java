package com.ryuqq.marketplace.adapter.out.persistence.shipment.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.shipment.condition.ShipmentConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.shipment.entity.QShipmentJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.shipment.entity.ShipmentJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.shipment.query.ShipmentSearchCriteria;
import com.ryuqq.marketplace.domain.shipment.query.ShipmentSortKey;
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

    public Optional<ShipmentJpaEntity> findByOrderItemId(Long orderItemId) {
        ShipmentJpaEntity entity =
                queryFactory
                        .selectFrom(shipment)
                        .where(
                                conditionBuilder.orderItemIdEq(orderItemId),
                                conditionBuilder.notDeleted())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    public List<ShipmentJpaEntity> findByOrderItemIds(List<Long> orderItemIds) {
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
