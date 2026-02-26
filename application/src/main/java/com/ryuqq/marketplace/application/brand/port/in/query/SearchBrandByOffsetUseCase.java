package com.ryuqq.marketplace.application.brand.port.in.query;

import com.ryuqq.marketplace.application.brand.dto.query.BrandSearchParams;
import com.ryuqq.marketplace.application.brand.dto.response.BrandPageResult;

/** 브랜드 검색 UseCase (Offset 기반 페이징). */
public interface SearchBrandByOffsetUseCase {
    BrandPageResult execute(BrandSearchParams params);
}
