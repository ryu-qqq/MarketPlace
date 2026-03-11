package com.ryuqq.marketplace.domain.shipment.event;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import java.time.Instant;

/** 배송 생성 이벤트. */
public record ShipmentCreatedEvent(String shipmentId, long orderItemId, Instant occurredAt)
        implements DomainEvent {}
