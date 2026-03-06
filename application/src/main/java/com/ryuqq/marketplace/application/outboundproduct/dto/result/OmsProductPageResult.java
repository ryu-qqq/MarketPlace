package com.ryuqq.marketplace.application.outboundproduct.dto.result;

import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.util.List;

/** OMS 상품 목록 페이징 결과 DTO. */
public record OmsProductPageResult(List<OmsProductListResult> results, PageMeta pageMeta) {

    public static OmsProductPageResult of(
            List<OmsProductListResult> results, int page, int size, long totalElements) {
        return new OmsProductPageResult(results, PageMeta.of(page, size, totalElements));
    }

    public static OmsProductPageResult empty(int size) {
        return new OmsProductPageResult(List.of(), PageMeta.empty(size));
    }
}
