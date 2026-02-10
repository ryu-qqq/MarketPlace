package com.ryuqq.marketplace.application.brandpreset.dto.response;

import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.util.List;

/** 브랜드 프리셋 페이징 조회 결과 DTO. */
public record BrandPresetPageResult(List<BrandPresetResult> results, PageMeta pageMeta) {

    public static BrandPresetPageResult of(List<BrandPresetResult> results, PageMeta pageMeta) {
        return new BrandPresetPageResult(results, pageMeta);
    }

    public static BrandPresetPageResult of(
            List<BrandPresetResult> results, int page, int size, long totalElements) {
        PageMeta pageMeta = PageMeta.of(page, size, totalElements);
        return new BrandPresetPageResult(results, pageMeta);
    }

    public static BrandPresetPageResult empty(int size) {
        return new BrandPresetPageResult(List.of(), PageMeta.empty(size));
    }

    public boolean isEmpty() {
        return results == null || results.isEmpty();
    }

    public int size() {
        return results != null ? results.size() : 0;
    }
}
