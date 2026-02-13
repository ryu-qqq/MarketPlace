package com.ryuqq.marketplace.domain.exchange.event;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.exchange.id.ExchangeClaimId;
import java.time.Instant;

/** 교환 철회 이벤트. */
public record ExchangeCancelledEvent(
        ExchangeClaimId exchangeClaimId, String orderId, Instant occurredAt)
        implements DomainEvent {}
