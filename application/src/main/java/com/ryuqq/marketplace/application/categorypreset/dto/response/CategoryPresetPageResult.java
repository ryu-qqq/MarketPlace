package com.ryuqq.marketplace.application.categorypreset.dto.response;

import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.util.List;

/** 카테고리 프리셋 페이징 조회 결과 DTO. */
public record CategoryPresetPageResult(List<CategoryPresetResult> results, PageMeta pageMeta) {

    public static CategoryPresetPageResult of(
            List<CategoryPresetResult> results, PageMeta pageMeta) {
        return new CategoryPresetPageResult(results, pageMeta);
    }

    public static CategoryPresetPageResult of(
            List<CategoryPresetResult> results, int page, int size, long totalElements) {
        PageMeta pageMeta = PageMeta.of(page, size, totalElements);
        return new CategoryPresetPageResult(results, pageMeta);
    }

    public static CategoryPresetPageResult empty(int size) {
        return new CategoryPresetPageResult(List.of(), PageMeta.empty(size));
    }

    public boolean isEmpty() {
        return results == null || results.isEmpty();
    }

    public int size() {
        return results != null ? results.size() : 0;
    }
}
