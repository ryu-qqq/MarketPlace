package com.ryuqq.marketplace.adapter.out.persistence.settlement.dto;

import java.time.LocalDate;

/** 일별 정산 집계 프로젝션 DTO. */
public record DailySettlementProjectionDto(
        LocalDate settlementDay,
        long entryCount,
        int totalSalesAmount,
        int totalCommissionAmount,
        int totalSettlementAmount) {}
