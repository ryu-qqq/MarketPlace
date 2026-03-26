package com.ryuqq.marketplace.domain.cancel.event;

import com.ryuqq.marketplace.domain.cancel.id.CancelId;
import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import java.time.Instant;

/** 취소 거절 이벤트. */
public record CancelRejectedEvent(CancelId cancelId, OrderItemId orderItemId, Instant occurredAt)
        implements DomainEvent {}
