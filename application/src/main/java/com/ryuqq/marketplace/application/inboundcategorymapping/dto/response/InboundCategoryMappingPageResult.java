package com.ryuqq.marketplace.application.inboundcategorymapping.dto.response;

import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.util.List;

/** 외부 카테고리 매핑 페이징 조회 결과 DTO. */
public record InboundCategoryMappingPageResult(
        List<InboundCategoryMappingResult> results, PageMeta pageMeta) {

    public static InboundCategoryMappingPageResult of(
            List<InboundCategoryMappingResult> results, PageMeta pageMeta) {
        return new InboundCategoryMappingPageResult(results, pageMeta);
    }

    public static InboundCategoryMappingPageResult of(
            List<InboundCategoryMappingResult> results, int page, int size, long totalElements) {
        PageMeta pageMeta = PageMeta.of(page, size, totalElements);
        return new InboundCategoryMappingPageResult(results, pageMeta);
    }

    public static InboundCategoryMappingPageResult empty(int size) {
        return new InboundCategoryMappingPageResult(List.of(), PageMeta.empty(size));
    }

    public boolean isEmpty() {
        return results == null || results.isEmpty();
    }

    public int size() {
        return results != null ? results.size() : 0;
    }
}
