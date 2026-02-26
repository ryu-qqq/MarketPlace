package com.ryuqq.marketplace.application.saleschannelbrand.dto.response;

import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.util.List;

/** 외부채널 브랜드 페이징 조회 결과 DTO. */
public record SalesChannelBrandPageResult(
        List<SalesChannelBrandResult> results, PageMeta pageMeta) {

    public static SalesChannelBrandPageResult of(
            List<SalesChannelBrandResult> results, PageMeta pageMeta) {
        return new SalesChannelBrandPageResult(results, pageMeta);
    }

    public static SalesChannelBrandPageResult of(
            List<SalesChannelBrandResult> results, int page, int size, long totalElements) {
        return new SalesChannelBrandPageResult(results, PageMeta.of(page, size, totalElements));
    }

    public static SalesChannelBrandPageResult empty(int size) {
        return new SalesChannelBrandPageResult(List.of(), PageMeta.empty(size));
    }
}
