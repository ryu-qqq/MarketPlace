package com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.condition;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.entity.QShipmentOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.entity.ShipmentOutboxJpaEntity;
import java.time.Instant;
import org.springframework.stereotype.Component;

/** 배송 아웃박스 QueryDSL 조건 빌더. */
@Component
public class ShipmentOutboxConditionBuilder {

    private static final QShipmentOutboxJpaEntity outbox =
            QShipmentOutboxJpaEntity.shipmentOutboxJpaEntity;

    public BooleanExpression statusPending() {
        return outbox.status.eq(ShipmentOutboxJpaEntity.Status.PENDING);
    }

    public BooleanExpression statusProcessing() {
        return outbox.status.eq(ShipmentOutboxJpaEntity.Status.PROCESSING);
    }

    public BooleanExpression createdAtBefore(Instant beforeTime) {
        if (beforeTime == null) {
            return null;
        }
        return outbox.createdAt.before(beforeTime);
    }

    public BooleanExpression updatedAtBefore(Instant beforeTime) {
        if (beforeTime == null) {
            return null;
        }
        return outbox.updatedAt.before(beforeTime);
    }
}
