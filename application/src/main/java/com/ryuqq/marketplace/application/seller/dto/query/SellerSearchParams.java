package com.ryuqq.marketplace.application.seller.dto.query;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;

/**
 * 셀러 검색 파라미터.
 *
 * <p>APP-DTO-003: SearchParams CommonSearchParams 포함 필수
 *
 * @param active 활성화 여부 필터
 * @param searchField 검색 필드 (null이면 전체 필드)
 * @param searchWord 검색어
 * @param searchParams 공통 검색 파라미터 (정렬, 페이징 등)
 */
public record SellerSearchParams(
        Boolean active, String searchField, String searchWord, CommonSearchParams searchParams) {

    public static SellerSearchParams of(
            Boolean active,
            String searchField,
            String searchWord,
            CommonSearchParams searchParams) {
        return new SellerSearchParams(active, searchField, searchWord, searchParams);
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
