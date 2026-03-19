package com.ryuqq.marketplace.domain.exchange.event;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.exchange.id.ExchangeClaimId;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import java.time.Instant;

/** 교환 상품 재배송 시작 이벤트. */
public record ExchangeShippingEvent(
        ExchangeClaimId exchangeClaimId,
        OrderItemId orderItemId,
        String linkedOrderId,
        Instant occurredAt)
        implements DomainEvent {}
