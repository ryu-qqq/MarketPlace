package com.ryuqq.marketplace.application.shipment.port.out.query;

import com.ryuqq.marketplace.domain.shipment.outbox.aggregate.ShipmentOutbox;
import java.time.Instant;
import java.util.List;

/** 배송 아웃박스 조회 포트. */
public interface ShipmentOutboxQueryPort {

    List<ShipmentOutbox> findPendingOutboxes(Instant beforeTime, int batchSize);

    List<ShipmentOutbox> findProcessingTimeoutOutboxes(Instant timeoutBefore, int batchSize);

    ShipmentOutbox getById(Long outboxId);
}
