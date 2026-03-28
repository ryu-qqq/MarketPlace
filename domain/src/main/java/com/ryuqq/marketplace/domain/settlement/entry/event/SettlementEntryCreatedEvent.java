package com.ryuqq.marketplace.domain.settlement.entry.event;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.settlement.entry.id.SettlementEntryId;
import com.ryuqq.marketplace.domain.settlement.entry.vo.EntryType;
import java.time.Instant;

/** 정산 원장 생성 이벤트. */
public record SettlementEntryCreatedEvent(
        SettlementEntryId entryId,
        long sellerId,
        EntryType entryType,
        Long orderItemId,
        Instant occurredAt)
        implements DomainEvent {}
