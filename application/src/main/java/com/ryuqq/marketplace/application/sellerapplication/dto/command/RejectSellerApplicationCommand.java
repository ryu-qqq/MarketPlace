package com.ryuqq.marketplace.application.sellerapplication.dto.command;

/**
 * 셀러 입점 신청 거절 Command.
 *
 * @param sellerApplicationId 거절할 신청 ID
 * @param rejectionReason 거절 사유
 * @param processedBy 처리자 식별자 (UUIDv7 또는 이메일)
 * @author ryu-qqq
 */
public record RejectSellerApplicationCommand(
        Long sellerApplicationId, String rejectionReason, String processedBy) {}
