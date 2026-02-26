package com.ryuqq.marketplace.application.sellerapplication.dto.command;

/**
 * 셀러 입점 신청 승인 Command.
 *
 * @param sellerApplicationId 승인할 신청 ID
 * @param processedBy 처리자 식별자 (UUIDv7 또는 이메일)
 * @author ryu-qqq
 */
public record ApproveSellerApplicationCommand(Long sellerApplicationId, String processedBy) {}
