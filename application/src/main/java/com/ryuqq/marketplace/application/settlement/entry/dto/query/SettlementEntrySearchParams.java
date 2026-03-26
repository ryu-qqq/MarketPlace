package com.ryuqq.marketplace.application.settlement.entry.dto.query;

import java.util.List;

/**
 * 정산 원장 목록 조회 파라미터.
 *
 * @param statuses 조회할 상태 목록 (PENDING, HOLD, CONFIRMED, SETTLED)
 * @param sellerIds 조회할 셀러 ID 목록
 * @param searchField 검색 필드 (ORDER_ID, ORDER_NUMBER, PRODUCT_NAME, BUYER_NAME)
 * @param searchWord 검색어
 * @param startDate 시작일 (YYYY-MM-DD)
 * @param endDate 종료일 (YYYY-MM-DD)
 * @param page 페이지 번호 (0-based)
 * @param size 페이지 크기
 */
public record SettlementEntrySearchParams(
        List<String> statuses,
        List<Long> sellerIds,
        String searchField,
        String searchWord,
        String startDate,
        String endDate,
        int page,
        int size) {}
