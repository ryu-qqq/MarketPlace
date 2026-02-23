package com.ryuqq.marketplace.application.inboundcategorymapping.port.in.query;

import com.ryuqq.marketplace.application.inboundcategorymapping.dto.query.InboundCategoryMappingSearchParams;
import com.ryuqq.marketplace.application.inboundcategorymapping.dto.response.InboundCategoryMappingPageResult;

/** 외부 카테고리 매핑 검색 UseCase. */
public interface SearchInboundCategoryMappingUseCase {

    InboundCategoryMappingPageResult execute(InboundCategoryMappingSearchParams params);
}
