package com.ryuqq.marketplace.domain.settlement.entry.event;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.settlement.entry.id.SettlementEntryId;
import java.time.Instant;

/** 정산 원장 확정 이벤트. */
public record SettlementEntryConfirmedEvent(
        SettlementEntryId entryId, long sellerId, Instant occurredAt) implements DomainEvent {}
