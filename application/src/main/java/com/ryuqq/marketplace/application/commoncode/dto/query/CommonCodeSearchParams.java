package com.ryuqq.marketplace.application.commoncode.dto.query;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;

/**
 * 공통 코드 검색 파라미터.
 *
 * <p>APP-DTO-003: SearchParams CommonSearchParams 포함 필수.
 *
 * <p>APP-DTO-002: Command/Query 인스턴스 메서드 금지.
 *
 * @param commonCodeTypeCode 공통 코드 타입 코드 (정확 일치, null이면 전체 조회)
 * @param active 활성화 여부 필터
 * @param searchParams 공통 검색 파라미터 (정렬, 페이징 등)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record CommonCodeSearchParams(
        String commonCodeTypeCode, Boolean active, CommonSearchParams searchParams) {

    public static CommonCodeSearchParams of(
            String commonCodeTypeCode, Boolean active, CommonSearchParams searchParams) {
        return new CommonCodeSearchParams(commonCodeTypeCode, active, searchParams);
    }

    // Delegate methods for convenience
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
