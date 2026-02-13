package com.ryuqq.marketplace.domain.exchange.event;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.exchange.id.ExchangeClaimId;
import java.time.Instant;

/** 교환 완료 이벤트. */
public record ExchangeCompletedEvent(
        ExchangeClaimId exchangeClaimId, String orderId, Instant occurredAt)
        implements DomainEvent {}
