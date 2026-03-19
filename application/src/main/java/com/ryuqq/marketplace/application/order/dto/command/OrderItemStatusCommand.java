package com.ryuqq.marketplace.application.order.dto.command;

import java.util.List;

/**
 * 주문상품 상태 전환 Command (배치).
 *
 * @param orderItemIds 대상 주문상품 ID 목록
 * @param changedBy 변경자
 */
public record OrderItemStatusCommand(
        List<String> orderItemIds,
        String changedBy) {}
