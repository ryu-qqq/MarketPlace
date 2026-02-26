package com.ryuqq.marketplace.domain.refund.event;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.refund.id.RefundClaimId;
import java.time.Instant;

/** 환불 수거 시작 이벤트. */
public record RefundCollectingEvent(RefundClaimId refundClaimId, String orderId, Instant occurredAt)
        implements DomainEvent {}
