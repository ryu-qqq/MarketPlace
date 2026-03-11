package com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.condition.ShipmentOutboxConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.entity.QShipmentOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.entity.ShipmentOutboxJpaEntity;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Repository;

/** 배송 아웃박스 QueryDSL Repository. */
@Repository
public class ShipmentOutboxQueryDslRepository {

    private static final QShipmentOutboxJpaEntity outbox =
            QShipmentOutboxJpaEntity.shipmentOutboxJpaEntity;
    private static final long MAX_FETCH_SIZE = 1000L;

    private final JPAQueryFactory queryFactory;
    private final ShipmentOutboxConditionBuilder conditionBuilder;

    public ShipmentOutboxQueryDslRepository(
            JPAQueryFactory queryFactory, ShipmentOutboxConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    public List<ShipmentOutboxJpaEntity> findPendingOutboxes(Instant beforeTime, int batchSize) {
        long limit = Math.min(batchSize, MAX_FETCH_SIZE);
        return queryFactory
                .selectFrom(outbox)
                .where(
                        conditionBuilder.statusPending(),
                        conditionBuilder.createdAtBefore(beforeTime))
                .orderBy(outbox.createdAt.asc())
                .limit(limit)
                .fetch();
    }

    public List<ShipmentOutboxJpaEntity> findProcessingTimeoutOutboxes(
            Instant timeoutBefore, int batchSize) {
        long limit = Math.min(batchSize, MAX_FETCH_SIZE);
        return queryFactory
                .selectFrom(outbox)
                .where(
                        conditionBuilder.statusProcessing(),
                        conditionBuilder.updatedAtBefore(timeoutBefore))
                .orderBy(outbox.updatedAt.asc())
                .limit(limit)
                .fetch();
    }
}
