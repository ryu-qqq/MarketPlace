package com.ryuqq.marketplace.domain.exchange.event;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.exchange.id.ExchangeClaimId;
import java.time.Instant;

/** 교환 상품 준비 시작 이벤트. */
public record ExchangePreparingEvent(
        ExchangeClaimId exchangeClaimId, String orderId, Instant occurredAt)
        implements DomainEvent {}
