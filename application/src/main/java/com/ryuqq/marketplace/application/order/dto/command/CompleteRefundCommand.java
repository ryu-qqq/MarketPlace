package com.ryuqq.marketplace.application.order.dto.command;

/**
 * 환불 완료 Command.
 *
 * @param orderId 주문 ID
 * @param changedBy 변경자
 */
public record CompleteRefundCommand(String orderId, String changedBy) {}
