package com.ryuqq.marketplace.application.brand.dto.response;

import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.util.List;

/** 브랜드 페이징 조회 결과 DTO. */
public record BrandPageResult(List<BrandResult> results, PageMeta pageMeta) {

    public static BrandPageResult of(List<BrandResult> results, PageMeta pageMeta) {
        return new BrandPageResult(results, pageMeta);
    }

    public static BrandPageResult of(
            List<BrandResult> results, int page, int size, long totalElements) {
        PageMeta pageMeta = PageMeta.of(page, size, totalElements);
        return new BrandPageResult(results, pageMeta);
    }

    public static BrandPageResult empty(int size) {
        return new BrandPageResult(List.of(), PageMeta.empty(size));
    }

    public boolean isEmpty() {
        return results == null || results.isEmpty();
    }

    public int size() {
        return results != null ? results.size() : 0;
    }
}
