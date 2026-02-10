package com.ryuqq.marketplace.application.saleschannelbrand.port.in.query;

import com.ryuqq.marketplace.application.saleschannelbrand.dto.query.SalesChannelBrandSearchParams;
import com.ryuqq.marketplace.application.saleschannelbrand.dto.response.SalesChannelBrandPageResult;

/** 외부채널 브랜드 검색 UseCase (Offset 기반 페이징). */
public interface SearchSalesChannelBrandByOffsetUseCase {
    SalesChannelBrandPageResult execute(SalesChannelBrandSearchParams params);
}
