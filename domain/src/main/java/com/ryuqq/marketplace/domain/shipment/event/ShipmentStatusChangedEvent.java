package com.ryuqq.marketplace.domain.shipment.event;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentStatus;
import java.time.Instant;

/** 배송 상태 변경 이벤트. */
public record ShipmentStatusChangedEvent(
        String shipmentId, ShipmentStatus fromStatus, ShipmentStatus toStatus, Instant occurredAt)
        implements DomainEvent {}
