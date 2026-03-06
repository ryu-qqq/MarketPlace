package com.ryuqq.marketplace.application.order.dto.command;

/**
 * 주문 발송 Command.
 *
 * @param orderId 주문 ID
 * @param changedBy 변경자
 */
public record ShipOrderCommand(String orderId, String changedBy) {}
