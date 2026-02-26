package com.ryuqq.marketplace.domain.seller.query;

import com.ryuqq.marketplace.domain.common.vo.QueryContext;

/**
 * Seller 검색 조건 Criteria.
 *
 * <p>셀러 목록 조회 시 사용하는 검색 조건과 페이징 정보를 정의합니다.
 *
 * @param active 활성화 상태 필터 (null이면 전체)
 * @param searchField 검색 필드 (null이면 전체 필드 검색)
 * @param searchWord 검색어 (null이면 전체)
 * @param queryContext 정렬 및 페이징 정보
 */
public record SellerSearchCriteria(
        Boolean active,
        SellerSearchField searchField,
        String searchWord,
        QueryContext<SellerSortKey> queryContext) {

    /**
     * 검색 조건 생성.
     *
     * @param active 활성화 상태 필터 (null이면 전체)
     * @param searchField 검색 필드 (null이면 전체 필드 검색)
     * @param searchWord 검색어 (null이면 전체)
     * @param queryContext 정렬 및 페이징 정보
     * @return SellerSearchCriteria
     */
    public static SellerSearchCriteria of(
            Boolean active,
            SellerSearchField searchField,
            String searchWord,
            QueryContext<SellerSortKey> queryContext) {
        return new SellerSearchCriteria(active, searchField, searchWord, queryContext);
    }

    /**
     * 기본 검색 조건 생성 (전체 조회, 등록순 내림차순).
     *
     * @return SellerSearchCriteria
     */
    public static SellerSearchCriteria defaultCriteria() {
        return new SellerSearchCriteria(
                null, null, null, QueryContext.defaultOf(SellerSortKey.defaultKey()));
    }

    /**
     * 활성화된 항목만 조회하는 조건 생성.
     *
     * @return SellerSearchCriteria
     */
    public static SellerSearchCriteria activeOnly() {
        return new SellerSearchCriteria(
                true, null, null, QueryContext.defaultOf(SellerSortKey.defaultKey()));
    }

    /** 검색 조건이 있는지 확인. */
    public boolean hasSearchCondition() {
        return searchWord != null && !searchWord.isBlank();
    }

    /** 특정 필드 검색인지 확인. */
    public boolean hasSearchField() {
        return searchField != null;
    }

    /** 활성화 상태 필터가 있는지 확인. */
    public boolean hasActiveFilter() {
        return active != null;
    }

    /** 페이지 크기 반환 (편의 메서드). */
    public int size() {
        return queryContext.size();
    }

    /** 오프셋 반환 (편의 메서드). */
    public long offset() {
        return queryContext.offset();
    }

    /** 현재 페이지 번호 반환 (편의 메서드). */
    public int page() {
        return queryContext.page();
    }
}
