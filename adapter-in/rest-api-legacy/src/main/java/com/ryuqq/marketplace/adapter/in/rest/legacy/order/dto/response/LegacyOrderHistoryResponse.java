package com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response;

import java.time.Instant;

/**
 * 세토프 OrderHistoryResponse 호환 응답 DTO.
 *
 * @param orderHistoryId 이력 ID
 * @param orderId 주문 ID
 * @param orderStatus 주문 상태
 * @param changeReason 변경 사유
 * @param changeDetailReason 변경 상세 사유
 * @param createdAt 생성 일시
 */
public record LegacyOrderHistoryResponse(
        long orderHistoryId,
        long orderId,
        String orderStatus,
        String changeReason,
        String changeDetailReason,
        Instant createdAt) {}
