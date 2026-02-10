package com.ryuqq.marketplace.application.saleschannel.dto.response;

import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.util.List;

/** 판매채널 페이징 조회 결과 DTO. */
public record SalesChannelPageResult(List<SalesChannelResult> results, PageMeta pageMeta) {

    public static SalesChannelPageResult of(List<SalesChannelResult> results, PageMeta pageMeta) {
        return new SalesChannelPageResult(results, pageMeta);
    }

    public static SalesChannelPageResult of(
            List<SalesChannelResult> results, int page, int size, long totalElements) {
        PageMeta pageMeta = PageMeta.of(page, size, totalElements);
        return new SalesChannelPageResult(results, pageMeta);
    }

    public static SalesChannelPageResult empty(int size) {
        return new SalesChannelPageResult(List.of(), PageMeta.empty(size));
    }

    public boolean isEmpty() {
        return results == null || results.isEmpty();
    }

    public int size() {
        return results != null ? results.size() : 0;
    }
}
