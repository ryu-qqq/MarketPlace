package com.ryuqq.marketplace.application.order.dto.command;

/**
 * 클레임 접수 Command.
 *
 * @param orderId 주문 ID
 * @param reason 클레임 사유
 * @param changedBy 변경자
 */
public record StartClaimCommand(String orderId, String reason, String changedBy) {}
