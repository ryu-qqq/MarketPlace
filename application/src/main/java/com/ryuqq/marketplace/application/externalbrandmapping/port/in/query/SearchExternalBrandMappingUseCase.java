package com.ryuqq.marketplace.application.externalbrandmapping.port.in.query;

import com.ryuqq.marketplace.application.externalbrandmapping.dto.query.ExternalBrandMappingSearchParams;
import com.ryuqq.marketplace.application.externalbrandmapping.dto.response.ExternalBrandMappingPageResult;

/** 외부 브랜드 매핑 검색 UseCase. */
public interface SearchExternalBrandMappingUseCase {

    ExternalBrandMappingPageResult execute(ExternalBrandMappingSearchParams params);
}
