package com.ryuqq.marketplace.domain.exchange.event;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.exchange.id.ExchangeClaimId;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import java.time.Instant;

/** 교환 수거 시작 이벤트. */
public record ExchangeCollectingEvent(
        ExchangeClaimId exchangeClaimId, OrderItemId orderItemId, Instant occurredAt)
        implements DomainEvent {}
