package com.ryuqq.marketplace.domain.cancel.event;

import com.ryuqq.marketplace.domain.cancel.id.CancelId;
import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import java.time.Instant;

/** 취소 거절 이벤트. */
public record CancelRejectedEvent(CancelId cancelId, String orderId, Instant occurredAt)
        implements DomainEvent {}
