package com.ryuqq.marketplace.application.channeloptionmapping.port.in.query;

import com.ryuqq.marketplace.application.channeloptionmapping.dto.query.ChannelOptionMappingSearchParams;
import com.ryuqq.marketplace.application.channeloptionmapping.dto.response.ChannelOptionMappingPageResult;

/** 채널 옵션 매핑 검색 UseCase (Offset 기반 페이징). */
public interface SearchChannelOptionMappingByOffsetUseCase {
    ChannelOptionMappingPageResult execute(ChannelOptionMappingSearchParams params);
}
