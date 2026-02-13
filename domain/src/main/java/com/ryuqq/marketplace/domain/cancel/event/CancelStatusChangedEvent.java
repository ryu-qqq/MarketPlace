package com.ryuqq.marketplace.domain.cancel.event;

import com.ryuqq.marketplace.domain.cancel.id.CancelId;
import com.ryuqq.marketplace.domain.cancel.vo.CancelStatus;
import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import java.time.Instant;

/** 취소 상태 변경 이벤트. */
public record CancelStatusChangedEvent(
        CancelId cancelId,
        String orderId,
        CancelStatus fromStatus,
        CancelStatus toStatus,
        Instant occurredAt)
        implements DomainEvent {}
