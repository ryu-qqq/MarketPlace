package com.ryuqq.marketplace.application.shipment.manager;

import com.ryuqq.marketplace.application.shipment.port.out.query.ShipmentOutboxQueryPort;
import com.ryuqq.marketplace.domain.shipment.outbox.aggregate.ShipmentOutbox;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 배송 아웃박스 Read Manager. */
@Component
public class ShipmentOutboxReadManager {

    private final ShipmentOutboxQueryPort queryPort;

    public ShipmentOutboxReadManager(ShipmentOutboxQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public List<ShipmentOutbox> findPendingOutboxes(Instant beforeTime, int batchSize) {
        return queryPort.findPendingOutboxes(beforeTime, batchSize);
    }

    @Transactional(readOnly = true)
    public List<ShipmentOutbox> findProcessingTimeoutOutboxes(
            Instant timeoutBefore, int batchSize) {
        return queryPort.findProcessingTimeoutOutboxes(timeoutBefore, batchSize);
    }

    @Transactional(readOnly = true)
    public ShipmentOutbox getById(Long outboxId) {
        return queryPort.getById(outboxId);
    }
}
