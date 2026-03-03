package com.ryuqq.marketplace.application.order.dto.command;

/**
 * 주문 구매확정 Command.
 *
 * @param orderId 주문 ID
 * @param changedBy 변경자
 */
public record ConfirmOrderCommand(String orderId, String changedBy) {}
