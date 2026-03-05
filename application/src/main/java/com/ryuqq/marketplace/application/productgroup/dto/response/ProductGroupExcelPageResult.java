package com.ryuqq.marketplace.application.productgroup.dto.response;

import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupExcelCompositeResult;
import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.util.List;

/** 상품 그룹 엑셀 페이징 조회 결과 DTO. */
public record ProductGroupExcelPageResult(
        List<ProductGroupExcelCompositeResult> results, PageMeta pageMeta) {

    public static ProductGroupExcelPageResult of(
            List<ProductGroupExcelCompositeResult> results, PageMeta pageMeta) {
        return new ProductGroupExcelPageResult(results, pageMeta);
    }

    public static ProductGroupExcelPageResult of(
            List<ProductGroupExcelCompositeResult> results,
            int page,
            int size,
            long totalElements) {
        PageMeta pageMeta = PageMeta.of(page, size, totalElements);
        return new ProductGroupExcelPageResult(results, pageMeta);
    }

    public static ProductGroupExcelPageResult empty(int size) {
        return new ProductGroupExcelPageResult(List.of(), PageMeta.empty(size));
    }

    public boolean isEmpty() {
        return results == null || results.isEmpty();
    }

    public int size() {
        return results != null ? results.size() : 0;
    }
}
