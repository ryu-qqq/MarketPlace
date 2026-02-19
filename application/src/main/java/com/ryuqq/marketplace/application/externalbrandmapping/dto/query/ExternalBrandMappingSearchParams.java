package com.ryuqq.marketplace.application.externalbrandmapping.dto.query;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import java.util.List;

/**
 * 외부 브랜드 매핑 검색 파라미터 DTO.
 *
 * @param externalSourceId 외부 소스 ID
 * @param statuses 상태 필터
 * @param searchField 검색 필드 (EXTERNAL_CODE, EXTERNAL_NAME)
 * @param searchWord 검색어
 * @param commonSearchParams 공통 검색 파라미터 (page, size, sortKey, sortDirection 등)
 */
public record ExternalBrandMappingSearchParams(
        Long externalSourceId,
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
