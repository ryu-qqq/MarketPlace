package com.ryuqq.marketplace.application.settlement.dto.response;

import java.time.LocalDate;

/**
 * 일별 정산 집계 결과.
 *
 * @param settlementDay 정산 예정일
 * @param entryCount Entry 건수
 * @param totalSalesAmount 총 판매 금액
 * @param totalCommissionAmount 총 수수료 금액
 * @param totalSettlementAmount 총 정산 금액
 */
public record DailySettlementResult(
        LocalDate settlementDay,
        long entryCount,
        int totalSalesAmount,
        int totalCommissionAmount,
        int totalSettlementAmount) {}
