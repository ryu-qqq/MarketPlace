package com.ryuqq.marketplace.application.order.dto.query;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import java.util.List;

/**
 * 주문 검색 파라미터.
 *
 * <p>APP-DTO-003: SearchParams CommonSearchParams 포함 필수
 *
 * @param statuses 주문 상태 필터
 * @param searchField 검색 필드 (null이면 전체 필드)
 * @param searchWord 검색어
 * @param dateField 날짜 검색 대상 필드
 * @param shopId 쇼핑몰 ID 필터
 * @param searchParams 공통 검색 파라미터 (정렬, 페이징 등)
 */
public record OrderSearchParams(
        List<String> statuses,
        String searchField,
        String searchWord,
        String dateField,
        Long shopId,
        CommonSearchParams searchParams) {

    public OrderSearchParams {
        statuses = statuses != null ? List.copyOf(statuses) : List.of();
    }
}
