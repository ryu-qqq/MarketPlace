package com.ryuqq.marketplace.application.productgroup.port.in.query;

import com.ryuqq.marketplace.application.productgroup.dto.query.ProductGroupSearchParams;
import com.ryuqq.marketplace.application.productgroup.dto.response.ProductGroupPageResult;

/** 상품 그룹 검색 UseCase (Offset 기반 페이징). */
public interface SearchProductGroupByOffsetUseCase {
    ProductGroupPageResult execute(ProductGroupSearchParams params);
}
