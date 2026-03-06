package com.ryuqq.marketplace.application.order.dto.command;

/**
 * 주문 배송완료 Command.
 *
 * @param orderId 주문 ID
 * @param changedBy 변경자
 */
public record DeliverOrderCommand(String orderId, String changedBy) {}
