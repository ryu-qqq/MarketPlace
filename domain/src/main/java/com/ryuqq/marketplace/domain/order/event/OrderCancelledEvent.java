package com.ryuqq.marketplace.domain.order.event;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.order.id.OrderId;
import java.time.Instant;

/** 주문 취소 이벤트. */
public record OrderCancelledEvent(OrderId orderId, Instant occurredAt) implements DomainEvent {}
