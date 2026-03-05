package com.ryuqq.marketplace.application.productgroup.port.in.query;

import com.ryuqq.marketplace.application.productgroup.dto.query.ProductGroupSearchParams;
import com.ryuqq.marketplace.application.productgroup.dto.response.ProductGroupExcelPageResult;

/** 상품 그룹 엑셀 다운로드용 검색 UseCase. */
public interface SearchProductGroupForExcelUseCase {
    ProductGroupExcelPageResult execute(ProductGroupSearchParams params);
}
