package com.ryuqq.marketplace.application.settlement.entry.dto.command;

/**
 * 역분개 Entry 생성 커맨드.
 *
 * @param orderItemId 주문 항목 ID
 * @param sellerId 셀러 ID
 * @param claimId 클레임 ID
 * @param claimType 클레임 유형 (CANCEL, REFUND, EXCHANGE)
 * @param salesAmount 원 판매 금액
 * @param commissionRate 수수료율 (basis point)
 */
public record CreateReversalEntryCommand(
        Long orderItemId,
        long sellerId,
        String claimId,
        String claimType,
        int salesAmount,
        int commissionRate) {}
