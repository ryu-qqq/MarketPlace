package com.ryuqq.marketplace.application.order.dto.command;

/**
 * 주문 취소 Command.
 *
 * @param orderId 주문 ID
 * @param reason 취소 사유
 * @param changedBy 변경자
 */
public record CancelOrderCommand(String orderId, String reason, String changedBy) {}
