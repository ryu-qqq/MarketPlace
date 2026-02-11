package com.ryuqq.marketplace.application.notice.dto.query;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;

/**
 * 고시정보 카테고리 검색 파라미터.
 *
 * @param active 활성 상태 필터 (null이면 전체 조회)
 * @param searchField 검색 필드 (code, nameKo)
 * @param searchWord 검색어
 * @param commonSearchParams 공통 검색 파라미터 (page, size, sortKey, sortDirection 등)
 */
public record NoticeCategorySearchParams(
        Boolean active,
        String searchField,
        String searchWord,
        CommonSearchParams commonSearchParams) {

    public static NoticeCategorySearchParams of(
            Boolean active,
            String searchField,
            String searchWord,
            CommonSearchParams commonSearchParams) {
        return new NoticeCategorySearchParams(active, searchField, searchWord, commonSearchParams);
    }
}
