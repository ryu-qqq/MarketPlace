package com.ryuqq.marketplace.domain.commoncodetype.query;

import com.ryuqq.marketplace.domain.common.vo.QueryContext;

/**
 * CommonCodeType 검색 조건 Criteria.
 *
 * <p>공통 코드 타입 목록 조회 시 사용하는 검색 조건과 페이징 정보를 정의합니다.
 *
 * <p><strong>사용 예시:</strong>
 *
 * <pre>{@code
 * // 활성화된 코드 타입만 조회 (등록순 정렬, 20개)
 * CommonCodeTypeSearchCriteria criteria = CommonCodeTypeSearchCriteria.of(
 *     true,
 *     null,
 *     null,
 *     null,
 *     QueryContext.defaultOf(CommonCodeTypeSortKey.CREATED_AT)
 * );
 *
 * // 검색 필드 + 검색어
 * CommonCodeTypeSearchCriteria criteria = CommonCodeTypeSearchCriteria.of(
 *     null,
 *     CommonCodeTypeSearchField.CODE,
 *     "결제",
 *     null,
 *     QueryContext.firstPage(CommonCodeTypeSortKey.CREATED_AT, SortDirection.DESC, 10)
 * );
 * }</pre>
 *
 * @param active 활성화 상태 필터 (null이면 전체)
 * @param searchField 검색 필드 (null이면 전체 필드 검색)
 * @param searchWord 검색어 (null/blank이면 검색 미적용)
 * @param type 공통 코드 값(CommonCodeValue) 필터 (null이면 미적용)
 * @param queryContext 정렬 및 페이징 정보
 */
public record CommonCodeTypeSearchCriteria(
        Boolean active,
        CommonCodeTypeSearchField searchField,
        String searchWord,
        String type,
        QueryContext<CommonCodeTypeSortKey> queryContext) {

    /**
     * 검색 조건 생성
     *
     * @param active 활성화 상태 필터 (null이면 전체)
     * @param searchField 검색 필드 (null이면 전체)
     * @param searchWord 검색어 (null이면 미적용)
     * @param type 공통 코드 값 필터 (null이면 미적용)
     * @param queryContext 정렬 및 페이징 정보
     * @return CommonCodeTypeSearchCriteria
     */
    public static CommonCodeTypeSearchCriteria of(
            Boolean active,
            CommonCodeTypeSearchField searchField,
            String searchWord,
            String type,
            QueryContext<CommonCodeTypeSortKey> queryContext) {
        return new CommonCodeTypeSearchCriteria(
                active, searchField, searchWord, type, queryContext);
    }

    /**
     * 기본 검색 조건 생성 (전체 조회, 등록순 내림차순)
     *
     * @return CommonCodeTypeSearchCriteria
     */
    public static CommonCodeTypeSearchCriteria defaultCriteria() {
        return new CommonCodeTypeSearchCriteria(
                null, null, null, null, QueryContext.defaultOf(CommonCodeTypeSortKey.defaultKey()));
    }

    /**
     * 활성화된 항목만 조회하는 조건 생성
     *
     * @return CommonCodeTypeSearchCriteria
     */
    public static CommonCodeTypeSearchCriteria activeOnly() {
        return new CommonCodeTypeSearchCriteria(
                true, null, null, null, QueryContext.defaultOf(CommonCodeTypeSortKey.defaultKey()));
    }

    /** 검색어 조건이 있는지 확인 */
    public boolean hasSearchWord() {
        return searchWord != null && !searchWord.isBlank();
    }

    /** 검색 필드가 지정되었는지 확인 */
    public boolean hasSearchField() {
        return searchField != null;
    }

    /** 공통 코드 값(type) 필터가 있는지 확인 */
    public boolean hasType() {
        return type != null && !type.isBlank();
    }

    /** 활성화 상태 필터가 있는지 확인 */
    public boolean hasActiveFilter() {
        return active != null;
    }

    /** 페이지 크기 반환 (편의 메서드) */
    public int size() {
        return queryContext.size();
    }

    /** 오프셋 반환 (편의 메서드) */
    public long offset() {
        return queryContext.offset();
    }

    /** 현재 페이지 번호 반환 (편의 메서드) */
    public int page() {
        return queryContext.page();
    }
}
