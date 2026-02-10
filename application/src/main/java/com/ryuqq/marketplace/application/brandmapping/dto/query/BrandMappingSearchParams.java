package com.ryuqq.marketplace.application.brandmapping.dto.query;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import java.util.List;
import java.util.Objects;

/** BrandMapping 검색 파라미터 DTO. */
public record BrandMappingSearchParams(
        List<Long> salesChannelBrandIds,
        List<Long> internalBrandIds,
        List<Long> salesChannelIds,
        List<String> statuses,
        String searchField,
        String searchWord,
        CommonSearchParams searchParams) {

    public BrandMappingSearchParams {
        Objects.requireNonNull(searchParams, "searchParams must not be null");
    }

    public static BrandMappingSearchParams of(
            List<Long> salesChannelBrandIds,
            List<Long> internalBrandIds,
            List<Long> salesChannelIds,
            List<String> statuses,
            String searchField,
            String searchWord,
            CommonSearchParams searchParams) {
        return new BrandMappingSearchParams(
                salesChannelBrandIds,
                internalBrandIds,
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
