package com.ryuqq.marketplace.domain.exchange.event;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.exchange.id.ExchangeClaimId;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeStatus;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import java.time.Instant;

/** 교환 클레임 상태 변경 이벤트. */
public record ExchangeClaimStatusChangedEvent(
        ExchangeClaimId exchangeClaimId,
        OrderItemId orderItemId,
        ExchangeStatus fromStatus,
        ExchangeStatus toStatus,
        Instant occurredAt)
        implements DomainEvent {}
