package com.ryuqq.marketplace.application.category.dto.response;

import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.util.List;

/** 카테고리 페이징 조회 결과 DTO. */
public record CategoryPageResult(List<CategoryResult> results, PageMeta pageMeta) {

    public static CategoryPageResult of(List<CategoryResult> results, PageMeta pageMeta) {
        return new CategoryPageResult(results, pageMeta);
    }

    public static CategoryPageResult of(
            List<CategoryResult> results, int page, int size, long totalElements) {
        PageMeta pageMeta = PageMeta.of(page, size, totalElements);
        return new CategoryPageResult(results, pageMeta);
    }

    public static CategoryPageResult empty(int size) {
        return new CategoryPageResult(List.of(), PageMeta.empty(size));
    }

    public boolean isEmpty() {
        return results == null || results.isEmpty();
    }

    public int size() {
        return results != null ? results.size() : 0;
    }
}
