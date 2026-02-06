package com.ryuqq.marketplace.application.commoncodetype.dto.query;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;

/**
 * 공통 코드 타입 검색 파라미터.
 *
 * <p>APP-DTO-003: SearchParams CommonSearchParams 포함 필수
 *
 * <p>APP-DTO-002: Command/Query 인스턴스 메서드 금지
 *
 * @param active 활성화 여부 필터
 * @param searchField 검색 필드 (null이면 전체 필드)
 * @param searchWord 검색어
 * @param type 공통 코드 값(CommonCodeValue) 필터 (선택, null이면 미적용)
 * @param searchParams 공통 검색 파라미터 (정렬, 페이징 등)
 */
public record CommonCodeTypeSearchParams(
        Boolean active,
        String searchField,
        String searchWord,
        String type,
        CommonSearchParams searchParams) {

    public static CommonCodeTypeSearchParams of(
            Boolean active,
            String searchField,
            String searchWord,
            String type,
            CommonSearchParams searchParams) {
        return new CommonCodeTypeSearchParams(active, searchField, searchWord, type, searchParams);
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
