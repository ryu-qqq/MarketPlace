package com.ryuqq.marketplace.application.shipment.dto.query;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import java.util.List;

/**
 * 배송 검색 파라미터.
 *
 * <p>APP-DTO-003: SearchParams CommonSearchParams 포함 필수
 *
 * @param statuses 배송 상태 필터
 * @param searchField 검색 필드 (null이면 전체 필드)
 * @param searchWord 검색어
 * @param dateField 날짜 검색 대상 필드
 * @param searchParams 공통 검색 파라미터 (정렬, 페이징 등)
 */
public record ShipmentSearchParams(
        List<String> statuses,
        String searchField,
        String searchWord,
        String dateField,
        CommonSearchParams searchParams) {

    public ShipmentSearchParams {
        statuses = statuses != null ? List.copyOf(statuses) : List.of();
    }

    public int page() {
        return searchParams.page();
    }

    public int size() {
        return searchParams.size();
    }

    public String sortKey() {
        return searchParams.sortKey();
    }

    public String sortDirection() {
        return searchParams.sortDirection();
    }
}
