package com.ryuqq.marketplace.domain.settlement.event;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.settlement.id.SettlementId;
import com.ryuqq.marketplace.domain.settlement.vo.SettlementStatus;
import java.time.Instant;

/** 정산 상태 변경 이벤트. */
public record SettlementStatusChangedEvent(
        SettlementId settlementId,
        String orderId,
        SettlementStatus fromStatus,
        SettlementStatus toStatus,
        Instant occurredAt)
        implements DomainEvent {}
