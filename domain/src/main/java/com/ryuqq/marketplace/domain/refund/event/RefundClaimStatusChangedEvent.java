package com.ryuqq.marketplace.domain.refund.event;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.refund.id.RefundClaimId;
import com.ryuqq.marketplace.domain.refund.vo.RefundStatus;
import java.time.Instant;

/** 환불 클레임 상태 변경 이벤트. */
public record RefundClaimStatusChangedEvent(
        RefundClaimId refundClaimId,
        OrderItemId orderItemId,
        RefundStatus fromStatus,
        RefundStatus toStatus,
        Instant occurredAt)
        implements DomainEvent {}
