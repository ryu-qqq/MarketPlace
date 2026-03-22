package com.ryuqq.marketplace.application.legacy.order.dto.result;

import java.time.Instant;

/**
 * 레거시 주문 이력 항목 결과.
 *
 * @param orderHistoryId 이력 ID
 * @param orderId 주문 ID
 * @param orderStatus 주문 상태
 * @param changeReason 변경 사유
 * @param changeDetailReason 변경 상세 사유
 * @param createdAt 생성 일시
 */
public record LegacyOrderHistoryResult(
        long orderHistoryId,
        long orderId,
        String orderStatus,
        String changeReason,
        String changeDetailReason,
        Instant createdAt) {}
