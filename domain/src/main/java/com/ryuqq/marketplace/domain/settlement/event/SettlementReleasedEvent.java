package com.ryuqq.marketplace.domain.settlement.event;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.settlement.id.SettlementId;
import java.time.Instant;

/** 정산 보류 해제 이벤트. */
public record SettlementReleasedEvent(
        SettlementId settlementId, String orderId, long sellerId, Instant occurredAt)
        implements DomainEvent {}
