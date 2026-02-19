package com.ryuqq.marketplace.application.externalbrandmapping.dto.query;

import java.util.List;

/** 외부 브랜드 매핑 검색 파라미터 DTO. */
public record ExternalBrandMappingSearchParams(
        Long externalSourceId, List<String> statuses, String searchWord, int page, int size) {

    public long offset() {
        return (long) page * size;
    }
}
