package com.ryuqq.marketplace.application.externalcategorymapping.port.in.query;

import com.ryuqq.marketplace.application.externalcategorymapping.dto.query.ExternalCategoryMappingSearchParams;
import com.ryuqq.marketplace.application.externalcategorymapping.dto.response.ExternalCategoryMappingPageResult;

/** 외부 카테고리 매핑 검색 UseCase. */
public interface SearchExternalCategoryMappingUseCase {

    ExternalCategoryMappingPageResult execute(ExternalCategoryMappingSearchParams params);
}
