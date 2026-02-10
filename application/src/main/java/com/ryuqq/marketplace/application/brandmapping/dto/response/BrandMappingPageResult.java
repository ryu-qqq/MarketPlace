package com.ryuqq.marketplace.application.brandmapping.dto.response;

import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.util.List;

/** 브랜드 매핑 페이징 조회 결과 DTO. */
public record BrandMappingPageResult(List<BrandMappingResult> results, PageMeta pageMeta) {

    public static BrandMappingPageResult of(List<BrandMappingResult> results, PageMeta pageMeta) {
        return new BrandMappingPageResult(results, pageMeta);
    }

    public static BrandMappingPageResult of(
            List<BrandMappingResult> results, int page, int size, long totalElements) {
        PageMeta pageMeta = PageMeta.of(page, size, totalElements);
        return new BrandMappingPageResult(results, pageMeta);
    }

    public static BrandMappingPageResult empty(int size) {
        return new BrandMappingPageResult(List.of(), PageMeta.empty(size));
    }

    public boolean isEmpty() {
        return results == null || results.isEmpty();
    }

    public int size() {
        return results != null ? results.size() : 0;
    }
}
