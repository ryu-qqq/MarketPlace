package com.ryuqq.marketplace.application.order.dto.command;

/**
 * 주문 발주확인 Command.
 *
 * @param orderId 주문 ID
 * @param changedBy 변경자
 */
public record PrepareOrderCommand(String orderId, String changedBy) {}
