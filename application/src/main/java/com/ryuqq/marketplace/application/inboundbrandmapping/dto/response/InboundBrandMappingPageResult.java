package com.ryuqq.marketplace.application.inboundbrandmapping.dto.response;

import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.util.List;

/** 외부 브랜드 매핑 페이징 조회 결과 DTO. */
public record InboundBrandMappingPageResult(
        List<InboundBrandMappingResult> results, PageMeta pageMeta) {

    public static InboundBrandMappingPageResult of(
            List<InboundBrandMappingResult> results, PageMeta pageMeta) {
        return new InboundBrandMappingPageResult(results, pageMeta);
    }

    public static InboundBrandMappingPageResult of(
            List<InboundBrandMappingResult> results, int page, int size, long totalElements) {
        PageMeta pageMeta = PageMeta.of(page, size, totalElements);
        return new InboundBrandMappingPageResult(results, pageMeta);
    }

    public static InboundBrandMappingPageResult empty(int size) {
        return new InboundBrandMappingPageResult(List.of(), PageMeta.empty(size));
    }

    public boolean isEmpty() {
        return results == null || results.isEmpty();
    }

    public int size() {
        return results != null ? results.size() : 0;
    }
}
