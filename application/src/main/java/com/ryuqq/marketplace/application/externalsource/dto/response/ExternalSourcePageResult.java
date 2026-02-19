package com.ryuqq.marketplace.application.externalsource.dto.response;

import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.util.List;

/** 외부 소스 페이징 조회 결과 DTO. */
public record ExternalSourcePageResult(List<ExternalSourceResult> results, PageMeta pageMeta) {

    public static ExternalSourcePageResult of(
            List<ExternalSourceResult> results, PageMeta pageMeta) {
        return new ExternalSourcePageResult(results, pageMeta);
    }

    public static ExternalSourcePageResult of(
            List<ExternalSourceResult> results, int page, int size, long totalElements) {
        PageMeta pageMeta = PageMeta.of(page, size, totalElements);
        return new ExternalSourcePageResult(results, pageMeta);
    }

    public static ExternalSourcePageResult empty(int size) {
        return new ExternalSourcePageResult(List.of(), PageMeta.empty(size));
    }

    public boolean isEmpty() {
        return results == null || results.isEmpty();
    }

    public int size() {
        return results != null ? results.size() : 0;
    }
}
