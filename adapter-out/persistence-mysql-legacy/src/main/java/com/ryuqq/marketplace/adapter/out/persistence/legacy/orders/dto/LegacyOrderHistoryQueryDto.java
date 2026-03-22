package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.dto;

import java.time.LocalDateTime;

/**
 * 레거시 주문 이력 flat projection DTO.
 *
 * @param orderHistoryId 이력 ID
 * @param orderId 주문 ID
 * @param orderStatus 주문 상태
 * @param changeReason 변경 사유
 * @param changeDetailReason 변경 상세 사유
 * @param insertDate 생성 일시
 */
public record LegacyOrderHistoryQueryDto(
        Long orderHistoryId,
        Long orderId,
        String orderStatus,
        String changeReason,
        String changeDetailReason,
        LocalDateTime insertDate) {}
