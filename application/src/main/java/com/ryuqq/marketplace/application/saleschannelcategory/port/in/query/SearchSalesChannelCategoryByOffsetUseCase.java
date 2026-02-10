package com.ryuqq.marketplace.application.saleschannelcategory.port.in.query;

import com.ryuqq.marketplace.application.saleschannelcategory.dto.query.SalesChannelCategorySearchParams;
import com.ryuqq.marketplace.application.saleschannelcategory.dto.response.SalesChannelCategoryPageResult;

/** 외부 채널 카테고리 검색 UseCase (Offset 기반 페이징). */
public interface SearchSalesChannelCategoryByOffsetUseCase {
    SalesChannelCategoryPageResult execute(SalesChannelCategorySearchParams params);
}
