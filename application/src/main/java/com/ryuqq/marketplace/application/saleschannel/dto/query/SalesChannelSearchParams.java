package com.ryuqq.marketplace.application.saleschannel.dto.query;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import java.util.List;
import java.util.Objects;

/** 판매채널 검색 파라미터 DTO. */
public record SalesChannelSearchParams(
        List<String> statuses,
        String searchField,
        String searchWord,
        CommonSearchParams searchParams) {

    public SalesChannelSearchParams {
        Objects.requireNonNull(searchParams, "searchParams must not be null");
    }

    public static SalesChannelSearchParams of(
            List<String> statuses,
            String searchField,
            String searchWord,
            CommonSearchParams searchParams) {
        return new SalesChannelSearchParams(statuses, searchField, searchWord, searchParams);
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
