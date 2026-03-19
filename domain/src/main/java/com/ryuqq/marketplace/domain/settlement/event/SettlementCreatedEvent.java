package com.ryuqq.marketplace.domain.settlement.event;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.settlement.id.SettlementId;
import java.time.Instant;

/** 정산 생성 이벤트. */
public record SettlementCreatedEvent(SettlementId settlementId, long sellerId, Instant occurredAt)
        implements DomainEvent {}
