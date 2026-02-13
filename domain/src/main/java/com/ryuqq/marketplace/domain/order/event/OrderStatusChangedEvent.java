package com.ryuqq.marketplace.domain.order.event;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.order.id.OrderId;
import com.ryuqq.marketplace.domain.order.vo.OrderStatus;
import java.time.Instant;

/** 주문 상태 변경 이벤트. */
public record OrderStatusChangedEvent(
        OrderId orderId, OrderStatus fromStatus, OrderStatus toStatus, Instant occurredAt)
        implements DomainEvent {}
