package com.ryuqq.marketplace.application.order.dto.command;

/**
 * 교환 완료 Command.
 *
 * @param orderId 주문 ID
 * @param changedBy 변경자
 */
public record CompleteExchangeCommand(String orderId, String changedBy) {}
