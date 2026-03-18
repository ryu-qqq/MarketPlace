package com.ryuqq.marketplace.domain.cancel.event;

import com.ryuqq.marketplace.domain.cancel.id.CancelId;
import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import java.time.Instant;

/** 취소 승인 이벤트. */
public record CancelApprovedEvent(CancelId cancelId, OrderItemId orderItemId, Instant occurredAt)
        implements DomainEvent {}
