package com.ryuqq.marketplace.application.legacy.order.dto.result;

/**
 * 레거시 주문 상태 변경 결과.
 *
 * @param orderId 주문 ID
 * @param userId 유저 ID
 * @param asIsOrderStatus 변경 전 상태
 * @param toBeOrderStatus 변경 후 상태
 * @param changeReason 변경 사유
 * @param changeDetailReason 변경 상세 사유
 */
public record LegacyOrderUpdateResult(
        long orderId,
        long userId,
        String asIsOrderStatus,
        String toBeOrderStatus,
        String changeReason,
        String changeDetailReason) {}
