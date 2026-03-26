package com.ryuqq.marketplace.application.settlement.dto.query;

import java.time.LocalDate;
import java.util.List;

/**
 * 일별 정산 조회 파라미터.
 *
 * @param startDate 시작일
 * @param endDate 종료일
 * @param sellerIds 셀러 ID 목록 (빈 목록이면 전체)
 */
public record DailySettlementSearchParams(
        LocalDate startDate, LocalDate endDate, List<Long> sellerIds) {}
