package com.ryuqq.marketplace.application.saleschannel.port.in.query;

import com.ryuqq.marketplace.application.saleschannel.dto.query.SalesChannelSearchParams;
import com.ryuqq.marketplace.application.saleschannel.dto.response.SalesChannelPageResult;

/** 판매채널 검색 UseCase (Offset 기반 페이징). */
public interface SearchSalesChannelByOffsetUseCase {
    SalesChannelPageResult execute(SalesChannelSearchParams params);
}
