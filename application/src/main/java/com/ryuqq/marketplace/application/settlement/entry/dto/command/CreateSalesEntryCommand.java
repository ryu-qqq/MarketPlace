package com.ryuqq.marketplace.application.settlement.entry.dto.command;

/**
 * 판매 Entry 생성 커맨드.
 *
 * @param orderItemId 주문 항목 ID
 * @param sellerId 셀러 ID
 * @param salesAmount 판매 금액
 * @param commissionRate 수수료율 (basis point, 1% = 100)
 */
public record CreateSalesEntryCommand(
        Long orderItemId, long sellerId, int salesAmount, int commissionRate) {}
