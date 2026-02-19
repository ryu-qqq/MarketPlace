package com.ryuqq.marketplace.application.externalcategorymapping.dto.query;

import java.util.List;

/** 외부 카테고리 매핑 검색 파라미터 DTO. */
public record ExternalCategoryMappingSearchParams(
        Long externalSourceId, List<String> statuses, String searchWord, int page, int size) {

    public long offset() {
        return (long) page * size;
    }
}
