package com.ryuqq.marketplace.application.externalsource.dto.query;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import java.util.List;

/**
 * 외부 소스 검색 파라미터 DTO.
 *
 * @param types 유형 필터 (CRAWLING, LEGACY, PARTNER)
 * @param statuses 상태 필터 (ACTIVE, INACTIVE)
 * @param searchField 검색 필드 (CODE, NAME)
 * @param searchWord 검색어
 * @param commonSearchParams 공통 검색 파라미터 (page, size, sortKey, sortDirection 등)
 */
public record ExternalSourceSearchParams(
        List<String> types,
        List<String> statuses,
        String searchField,
        String searchWord,
        CommonSearchParams commonSearchParams) {

    public int page() {
        return commonSearchParams.page();
    }

    public int size() {
        return commonSearchParams.size();
    }
}
