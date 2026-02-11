package com.ryuqq.marketplace.application.canonicaloption.dto.query;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;

/**
 * 캐노니컬 옵션 그룹 검색 파라미터.
 *
 * @param active 활성 상태 필터 (null이면 전체 조회)
 * @param searchField 검색 필드 (code, nameKo)
 * @param searchWord 검색어
 * @param commonSearchParams 공통 검색 파라미터 (page, size, sortKey, sortDirection 등)
 */
public record CanonicalOptionGroupSearchParams(
        Boolean active,
        String searchField,
        String searchWord,
        CommonSearchParams commonSearchParams) {

    public static CanonicalOptionGroupSearchParams of(
            Boolean active,
            String searchField,
            String searchWord,
            CommonSearchParams commonSearchParams) {
        return new CanonicalOptionGroupSearchParams(
                active, searchField, searchWord, commonSearchParams);
    }
}
