package com.ryuqq.marketplace.application.brandmapping.port.in.query;

import com.ryuqq.marketplace.application.brandmapping.dto.query.BrandMappingSearchParams;
import com.ryuqq.marketplace.application.brandmapping.dto.response.BrandMappingPageResult;

/** 브랜드 매핑 검색 UseCase (Offset 기반 페이징). */
public interface SearchBrandMappingByOffsetUseCase {
    BrandMappingPageResult execute(BrandMappingSearchParams params);
}
