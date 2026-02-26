package com.ryuqq.marketplace.application.saleschannelbrand.dto.query;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import java.util.List;
import java.util.Objects;

/** 외부채널 브랜드 검색 파라미터 DTO. */
public record SalesChannelBrandSearchParams(
        List<Long> salesChannelIds,
        List<String> statuses,
        String searchField,
        String searchWord,
        CommonSearchParams searchParams) {

    public SalesChannelBrandSearchParams {
        salesChannelIds = salesChannelIds != null ? List.copyOf(salesChannelIds) : List.of();
        Objects.requireNonNull(searchParams, "searchParams must not be null");
    }

    public static SalesChannelBrandSearchParams of(
            List<Long> salesChannelIds,
            List<String> statuses,
            String searchField,
            String searchWord,
            CommonSearchParams searchParams) {
        return new SalesChannelBrandSearchParams(
                salesChannelIds, statuses, searchField, searchWord, searchParams);
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
