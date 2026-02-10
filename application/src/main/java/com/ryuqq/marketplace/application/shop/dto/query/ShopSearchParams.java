package com.ryuqq.marketplace.application.shop.dto.query;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import java.util.List;
import java.util.Objects;

/** Shop 검색 파라미터 DTO. */
public record ShopSearchParams(
        Long salesChannelId,
        List<String> statuses,
        String searchField,
        String searchWord,
        CommonSearchParams searchParams) {

    public ShopSearchParams {
        Objects.requireNonNull(searchParams, "searchParams must not be null");
    }

    public static ShopSearchParams of(
            Long salesChannelId,
            List<String> statuses,
            String searchField,
            String searchWord,
            CommonSearchParams searchParams) {
        return new ShopSearchParams(
                salesChannelId, statuses, searchField, searchWord, searchParams);
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
