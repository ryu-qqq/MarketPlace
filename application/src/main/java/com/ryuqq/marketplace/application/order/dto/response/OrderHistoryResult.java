package com.ryuqq.marketplace.application.order.dto.response;

import java.time.Instant;

/**
 * 주문 변경 이력 조회 결과.
 *
 * @param historyId 이력 ID
 * @param fromStatus 이전 상태
 * @param toStatus 변경된 상태
 * @param changedBy 변경자
 * @param reason 변경 사유
 * @param changedAt 변경 일시
 */
public record OrderHistoryResult(
        long historyId,
        String fromStatus,
        String toStatus,
        String changedBy,
        String reason,
        Instant changedAt) {}
