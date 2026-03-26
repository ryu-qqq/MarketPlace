package com.ryuqq.marketplace.application.settlement.dto.command;

import java.time.LocalDate;

/**
 * Settlement 집계 커맨드.
 *
 * @param sellerId 대상 셀러 ID
 * @param periodStartDate 정산 기간 시작일
 * @param periodEndDate 정산 기간 종료일
 * @param settlementCycle 정산 주기
 */
public record AggregateSettlementCommand(
        long sellerId,
        LocalDate periodStartDate,
        LocalDate periodEndDate,
        String settlementCycle) {}
