package com.ryuqq.marketplace.application.channeloptionmapping.dto.response;

import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.util.List;

/** 채널 옵션 매핑 페이징 조회 결과 DTO. */
public record ChannelOptionMappingPageResult(
        List<ChannelOptionMappingResult> results, PageMeta pageMeta) {

    public static ChannelOptionMappingPageResult of(
            List<ChannelOptionMappingResult> results, PageMeta pageMeta) {
        return new ChannelOptionMappingPageResult(results, pageMeta);
    }

    public static ChannelOptionMappingPageResult of(
            List<ChannelOptionMappingResult> results, int page, int size, long totalElements) {
        PageMeta pageMeta = PageMeta.of(page, size, totalElements);
        return new ChannelOptionMappingPageResult(results, pageMeta);
    }

    public static ChannelOptionMappingPageResult empty(int size) {
        return new ChannelOptionMappingPageResult(List.of(), PageMeta.empty(size));
    }

    public boolean isEmpty() {
        return results == null || results.isEmpty();
    }

    public int size() {
        return results != null ? results.size() : 0;
    }
}
