package com.ryuqq.marketplace.application.categorymapping.dto.query;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import java.util.List;
import java.util.Objects;

/** CategoryMapping 검색 파라미터 DTO. */
public record CategoryMappingSearchParams(
        List<Long> salesChannelCategoryIds,
        List<Long> internalCategoryIds,
        List<Long> salesChannelIds,
        List<String> statuses,
        String searchField,
        String searchWord,
        CommonSearchParams searchParams) {

    public CategoryMappingSearchParams {
        Objects.requireNonNull(searchParams, "searchParams must not be null");
    }

    public static CategoryMappingSearchParams of(
            List<Long> salesChannelCategoryIds,
            List<Long> internalCategoryIds,
            List<Long> salesChannelIds,
            List<String> statuses,
            String searchField,
            String searchWord,
            CommonSearchParams searchParams) {
        return new CategoryMappingSearchParams(
                salesChannelCategoryIds,
                internalCategoryIds,
                salesChannelIds,
                statuses,
                searchField,
                searchWord,
                searchParams);
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
