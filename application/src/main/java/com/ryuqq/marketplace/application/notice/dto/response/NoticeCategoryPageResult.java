package com.ryuqq.marketplace.application.notice.dto.response;

import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.util.List;

/** 고시정보 카테고리 페이징 조회 결과 DTO. */
public record NoticeCategoryPageResult(List<NoticeCategoryResult> results, PageMeta pageMeta) {

    public static NoticeCategoryPageResult of(
            List<NoticeCategoryResult> results, PageMeta pageMeta) {
        return new NoticeCategoryPageResult(results, pageMeta);
    }

    public static NoticeCategoryPageResult of(
            List<NoticeCategoryResult> results, int page, int size, long totalElements) {
        PageMeta pageMeta = PageMeta.of(page, size, totalElements);
        return new NoticeCategoryPageResult(results, pageMeta);
    }

    public static NoticeCategoryPageResult empty(int size) {
        return new NoticeCategoryPageResult(List.of(), PageMeta.empty(size));
    }

    public boolean isEmpty() {
        return results == null || results.isEmpty();
    }

    public int size() {
        return results != null ? results.size() : 0;
    }
}
