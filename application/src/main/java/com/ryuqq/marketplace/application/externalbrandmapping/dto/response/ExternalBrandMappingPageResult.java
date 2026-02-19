package com.ryuqq.marketplace.application.externalbrandmapping.dto.response;

import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.util.List;

/** 외부 브랜드 매핑 페이징 조회 결과 DTO. */
public record ExternalBrandMappingPageResult(
        List<ExternalBrandMappingResult> results, PageMeta pageMeta) {

    public static ExternalBrandMappingPageResult of(
            List<ExternalBrandMappingResult> results, PageMeta pageMeta) {
        return new ExternalBrandMappingPageResult(results, pageMeta);
    }

    public static ExternalBrandMappingPageResult of(
            List<ExternalBrandMappingResult> results, int page, int size, long totalElements) {
        PageMeta pageMeta = PageMeta.of(page, size, totalElements);
        return new ExternalBrandMappingPageResult(results, pageMeta);
    }

    public static ExternalBrandMappingPageResult empty(int size) {
        return new ExternalBrandMappingPageResult(List.of(), PageMeta.empty(size));
    }

    public boolean isEmpty() {
        return results == null || results.isEmpty();
    }

    public int size() {
        return results != null ? results.size() : 0;
    }
}
