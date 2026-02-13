package com.ryuqq.marketplace.domain.refund.event;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.refund.id.RefundClaimId;
import java.time.Instant;

/** 환불 완료 이벤트. */
public record RefundCompletedEvent(RefundClaimId refundClaimId, String orderId, Instant occurredAt)
        implements DomainEvent {}
