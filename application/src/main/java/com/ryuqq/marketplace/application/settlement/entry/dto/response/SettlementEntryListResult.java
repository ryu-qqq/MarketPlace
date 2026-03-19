package com.ryuqq.marketplace.application.settlement.entry.dto.response;

import java.time.Instant;

/**
 * 정산 원장 목록 조회 결과 항목.
 *
 * @param entryId 정산 원장 ID
 * @param entryStatus 상태 (PENDING, HOLD, CONFIRMED, SETTLED)
 * @param sellerId 셀러 ID
 * @param entryType 유형 (SALES, CANCEL, REFUND, EXCHANGE_OUT, EXCHANGE_IN, ADJUSTMENT)
 * @param orderItemId 주문 항목 ID
 * @param salesAmount 판매 금액
 * @param commissionRate 수수료율 (basis point, 1% = 100)
 * @param commissionAmount 수수료 금액
 * @param settlementAmount 정산 금액
 * @param claimId 클레임 ID (역분개인 경우)
 * @param claimType 클레임 유형 (역분개인 경우)
 * @param eligibleAt 정산 가능 시점
 * @param holdReason 보류 사유 (HOLD 상태인 경우)
 * @param holdAt 보류 처리 시각 (HOLD 상태인 경우)
 * @param createdAt 생성 시각
 */
public record SettlementEntryListResult(
        String entryId,
        String entryStatus,
        long sellerId,
        String entryType,
        String orderItemId,
        int salesAmount,
        int commissionRate,
        int commissionAmount,
        int settlementAmount,
        String claimId,
        String claimType,
        Instant eligibleAt,
        String holdReason,
        Instant holdAt,
        Instant createdAt) {}
