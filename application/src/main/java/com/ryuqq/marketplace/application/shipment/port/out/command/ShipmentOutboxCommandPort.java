package com.ryuqq.marketplace.application.shipment.port.out.command;

import com.ryuqq.marketplace.domain.shipment.outbox.aggregate.ShipmentOutbox;
import java.util.List;

/** 배송 아웃박스 커맨드 포트. */
public interface ShipmentOutboxCommandPort {

    Long persist(ShipmentOutbox outbox);

    void persistAll(List<ShipmentOutbox> outboxes);
}
