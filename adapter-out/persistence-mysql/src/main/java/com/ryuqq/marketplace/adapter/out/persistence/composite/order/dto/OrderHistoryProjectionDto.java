package com.ryuqq.marketplace.adapter.out.persistence.composite.order.dto;

import java.time.Instant;

/** 주문 히스토리 프로젝션. */
public record OrderHistoryProjectionDto(
        Long historyId,
        String fromStatus,
        String toStatus,
        String changedBy,
        String reason,
        Instant changedAt) {}
