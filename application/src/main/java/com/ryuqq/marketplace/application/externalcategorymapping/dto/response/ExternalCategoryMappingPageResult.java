package com.ryuqq.marketplace.application.externalcategorymapping.dto.response;

import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.util.List;

/** 외부 카테고리 매핑 페이징 조회 결과 DTO. */
public record ExternalCategoryMappingPageResult(
        List<ExternalCategoryMappingResult> results, PageMeta pageMeta) {

    public static ExternalCategoryMappingPageResult of(
            List<ExternalCategoryMappingResult> results, PageMeta pageMeta) {
        return new ExternalCategoryMappingPageResult(results, pageMeta);
    }

    public static ExternalCategoryMappingPageResult of(
            List<ExternalCategoryMappingResult> results, int page, int size, long totalElements) {
        PageMeta pageMeta = PageMeta.of(page, size, totalElements);
        return new ExternalCategoryMappingPageResult(results, pageMeta);
    }

    public static ExternalCategoryMappingPageResult empty(int size) {
        return new ExternalCategoryMappingPageResult(List.of(), PageMeta.empty(size));
    }

    public boolean isEmpty() {
        return results == null || results.isEmpty();
    }

    public int size() {
        return results != null ? results.size() : 0;
    }
}
