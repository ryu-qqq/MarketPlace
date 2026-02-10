package com.ryuqq.marketplace.application.saleschannelcategory.dto.query;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import java.util.List;
import java.util.Objects;

/** 외부 채널 카테고리 검색 파라미터 DTO. */
public record SalesChannelCategorySearchParams(
        List<Long> salesChannelIds,
        List<String> statuses,
        String searchField,
        String searchWord,
        CommonSearchParams searchParams) {

    public SalesChannelCategorySearchParams {
        salesChannelIds = salesChannelIds != null ? List.copyOf(salesChannelIds) : List.of();
        Objects.requireNonNull(searchParams, "searchParams must not be null");
    }

    public static SalesChannelCategorySearchParams of(
            List<Long> salesChannelIds,
            List<String> statuses,
            String searchField,
            String searchWord,
            CommonSearchParams searchParams) {
        return new SalesChannelCategorySearchParams(
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
