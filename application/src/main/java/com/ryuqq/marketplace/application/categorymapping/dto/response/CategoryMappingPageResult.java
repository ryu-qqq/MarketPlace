package com.ryuqq.marketplace.application.categorymapping.dto.response;

import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.util.List;

/** 카테고리 매핑 페이징 조회 결과 DTO. */
public record CategoryMappingPageResult(List<CategoryMappingResult> results, PageMeta pageMeta) {

    public static CategoryMappingPageResult of(
            List<CategoryMappingResult> results, PageMeta pageMeta) {
        return new CategoryMappingPageResult(results, pageMeta);
    }

    public static CategoryMappingPageResult of(
            List<CategoryMappingResult> results, int page, int size, long totalElements) {
        PageMeta pageMeta = PageMeta.of(page, size, totalElements);
        return new CategoryMappingPageResult(results, pageMeta);
    }

    public static CategoryMappingPageResult empty(int size) {
        return new CategoryMappingPageResult(List.of(), PageMeta.empty(size));
    }

    public boolean isEmpty() {
        return results == null || results.isEmpty();
    }

    public int size() {
        return results != null ? results.size() : 0;
    }
}
