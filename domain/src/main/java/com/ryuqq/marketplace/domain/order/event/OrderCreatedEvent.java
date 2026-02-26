package com.ryuqq.marketplace.domain.order.event;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.order.id.OrderId;
import com.ryuqq.marketplace.domain.order.id.OrderNumber;
import java.time.Instant;

/** 주문 생성 이벤트. */
public record OrderCreatedEvent(OrderId orderId, OrderNumber orderNumber, Instant occurredAt)
        implements DomainEvent {}
