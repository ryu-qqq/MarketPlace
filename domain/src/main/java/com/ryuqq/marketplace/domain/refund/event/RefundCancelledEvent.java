package com.ryuqq.marketplace.domain.refund.event;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.refund.id.RefundClaimId;
import java.time.Instant;

/** 환불 철회 이벤트. */
public record RefundCancelledEvent(RefundClaimId refundClaimId, String orderId, Instant occurredAt)
        implements DomainEvent {}
