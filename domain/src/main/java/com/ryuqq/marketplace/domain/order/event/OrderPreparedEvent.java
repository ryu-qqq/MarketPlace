package com.ryuqq.marketplace.domain.order.event;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.order.id.OrderId;
import java.time.Instant;

/** 주문 상품준비 이벤트. */
public record OrderPreparedEvent(OrderId orderId, Instant occurredAt) implements DomainEvent {}
