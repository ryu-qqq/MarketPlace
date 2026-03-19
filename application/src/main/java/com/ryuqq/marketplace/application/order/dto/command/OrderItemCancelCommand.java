package com.ryuqq.marketplace.application.order.dto.command;

import java.util.List;

/**
 * 주문상품 취소 Command (배치).
 *
 * @param orderItemIds 대상 주문상품 ID 목록
 * @param reason 취소 사유
 * @param changedBy 변경자
 */
public record OrderItemCancelCommand(List<String> orderItemIds, String reason, String changedBy) {}
