package com.ryuqq.marketplace.domain.cancel.event;

import com.ryuqq.marketplace.domain.cancel.id.CancelId;
import com.ryuqq.marketplace.domain.cancel.vo.CancelType;
import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import java.time.Instant;

/** 취소 생성 이벤트. */
public record CancelCreatedEvent(
        CancelId cancelId, String orderId, CancelType cancelType, Instant occurredAt)
        implements DomainEvent {}
