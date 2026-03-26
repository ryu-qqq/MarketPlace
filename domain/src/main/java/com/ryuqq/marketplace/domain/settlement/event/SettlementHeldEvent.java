package com.ryuqq.marketplace.domain.settlement.event;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.settlement.id.SettlementId;
import java.time.Instant;

/** 정산 보류 이벤트. */
public record SettlementHeldEvent(
        SettlementId settlementId, long sellerId, String holdReason, Instant occurredAt)
        implements DomainEvent {}
