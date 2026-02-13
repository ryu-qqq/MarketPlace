package com.ryuqq.marketplace.application.productgroup.dto.response;

import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupListCompositeResult;
import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.util.List;

/** 상품 그룹 페이징 조회 결과 DTO. */
public record ProductGroupPageResult(
        List<ProductGroupListCompositeResult> results, PageMeta pageMeta) {

    public static ProductGroupPageResult of(
            List<ProductGroupListCompositeResult> results, PageMeta pageMeta) {
        return new ProductGroupPageResult(results, pageMeta);
    }

    public static ProductGroupPageResult of(
            List<ProductGroupListCompositeResult> results, int page, int size, long totalElements) {
        PageMeta pageMeta = PageMeta.of(page, size, totalElements);
        return new ProductGroupPageResult(results, pageMeta);
    }

    public static ProductGroupPageResult empty(int size) {
        return new ProductGroupPageResult(List.of(), PageMeta.empty(size));
    }

    public boolean isEmpty() {
        return results == null || results.isEmpty();
    }

    public int size() {
        return results != null ? results.size() : 0;
    }
}
