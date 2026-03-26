package com.ryuqq.marketplace.domain.settlement.entry.event;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.settlement.entry.id.SettlementEntryId;
import com.ryuqq.marketplace.domain.settlement.entry.vo.EntryStatus;
import java.time.Instant;

/** 정산 원장 상태 변경 이벤트. */
public record SettlementEntryStatusChangedEvent(
        SettlementEntryId entryId, EntryStatus fromStatus, EntryStatus toStatus, Instant occurredAt)
        implements DomainEvent {}
