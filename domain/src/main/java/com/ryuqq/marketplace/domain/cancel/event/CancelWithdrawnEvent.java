package com.ryuqq.marketplace.domain.cancel.event;

import com.ryuqq.marketplace.domain.cancel.id.CancelId;
import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import java.time.Instant;

/** 취소 철회(구매자가 취소 요청 취소) 이벤트. */
public record CancelWithdrawnEvent(CancelId cancelId, OrderItemId orderItemId, Instant occurredAt)
        implements DomainEvent {}
