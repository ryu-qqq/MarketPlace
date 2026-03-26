package com.ryuqq.marketplace.domain.settlement.vo;

import java.time.LocalDate;

/**
 * 정산 기간.
 *
 * @param startDate 정산 시작일 (포함)
 * @param endDate 정산 종료일 (포함)
 * @param cycle 정산 주기
 */
public record SettlementPeriod(LocalDate startDate, LocalDate endDate, SettlementCycle cycle) {

    public SettlementPeriod {
        if (startDate == null) {
            throw new IllegalArgumentException("startDate는 null일 수 없습니다");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("endDate는 null일 수 없습니다");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate는 endDate 이전이어야 합니다");
        }
        if (cycle == null) {
            throw new IllegalArgumentException("cycle은 null일 수 없습니다");
        }
    }

    public static SettlementPeriod of(
            LocalDate startDate, LocalDate endDate, SettlementCycle cycle) {
        return new SettlementPeriod(startDate, endDate, cycle);
    }
}
