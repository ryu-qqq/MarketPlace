package com.ryuqq.marketplace.application.channeloptionmapping.dto.query;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;

/** 채널 옵션 매핑 검색 파라미터 DTO. */
public record ChannelOptionMappingSearchParams(
        Long salesChannelId, Long canonicalOptionGroupId, CommonSearchParams searchParams) {

    public static ChannelOptionMappingSearchParams of(
            Long salesChannelId, Long canonicalOptionGroupId, CommonSearchParams searchParams) {
        return new ChannelOptionMappingSearchParams(
                salesChannelId, canonicalOptionGroupId, searchParams);
    }

    public int page() {
        return searchParams.page();
    }

    public int size() {
        return searchParams.size();
    }

    public String sortKey() {
        return searchParams.sortKey();
    }

    public String sortDirection() {
        return searchParams.sortDirection();
    }
}
