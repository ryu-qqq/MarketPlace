package com.ryuqq.marketplace.application.saleschannelcategory.dto.response;

import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.util.List;

/** 외부 채널 카테고리 페이징 조회 결과 DTO. */
public record SalesChannelCategoryPageResult(
        List<SalesChannelCategoryResult> results, PageMeta pageMeta) {

    public static SalesChannelCategoryPageResult of(
            List<SalesChannelCategoryResult> results, PageMeta pageMeta) {
        return new SalesChannelCategoryPageResult(results, pageMeta);
    }

    public static SalesChannelCategoryPageResult of(
            List<SalesChannelCategoryResult> results, int page, int size, long totalElements) {
        PageMeta pageMeta = PageMeta.of(page, size, totalElements);
        return new SalesChannelCategoryPageResult(results, pageMeta);
    }

    public static SalesChannelCategoryPageResult empty(int size) {
        return new SalesChannelCategoryPageResult(List.of(), PageMeta.empty(size));
    }

    public boolean isEmpty() {
        return results == null || results.isEmpty();
    }

    public int size() {
        return results != null ? results.size() : 0;
    }
}
