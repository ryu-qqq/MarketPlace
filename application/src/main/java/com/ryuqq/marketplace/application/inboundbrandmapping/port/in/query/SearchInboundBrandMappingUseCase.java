package com.ryuqq.marketplace.application.inboundbrandmapping.port.in.query;

import com.ryuqq.marketplace.application.inboundbrandmapping.dto.query.InboundBrandMappingSearchParams;
import com.ryuqq.marketplace.application.inboundbrandmapping.dto.response.InboundBrandMappingPageResult;

/** 외부 브랜드 매핑 검색 UseCase. */
public interface SearchInboundBrandMappingUseCase {

    InboundBrandMappingPageResult execute(InboundBrandMappingSearchParams params);
}
