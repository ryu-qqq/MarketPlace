package com.ryuqq.marketplace.domain.refund.event;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.refund.id.RefundClaimId;
import java.time.Instant;

/** 환불 클레임 생성 이벤트. */
public record RefundClaimCreatedEvent(
        RefundClaimId refundClaimId, OrderItemId orderItemId, Instant occurredAt) implements DomainEvent {}
