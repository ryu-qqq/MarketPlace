package com.ryuqq.marketplace.domain.cancel.event;

import com.ryuqq.marketplace.domain.cancel.id.CancelId;
import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import java.time.Instant;

/** 취소 완료(환불 처리 완료) 이벤트. */
public record CancelCompletedEvent(CancelId cancelId, String orderId, Instant occurredAt)
        implements DomainEvent {}
