package com.ryuqq.marketplace.domain.cancel.event;

import com.ryuqq.marketplace.domain.cancel.id.CancelId;
import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import java.time.Instant;

/** 취소 승인 이벤트. */
public record CancelApprovedEvent(CancelId cancelId, String orderId, Instant occurredAt)
        implements DomainEvent {}
