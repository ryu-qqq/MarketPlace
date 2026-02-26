package com.ryuqq.marketplace.application.brand.dto.query;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import java.util.List;

/** 브랜드 검색 파라미터 DTO. */
public record BrandSearchParams(
        List<String> statuses,
        String searchField,
        String searchWord,
        CommonSearchParams searchParams) {

    public static BrandSearchParams of(
            List<String> statuses,
            String searchField,
            String searchWord,
            CommonSearchParams searchParams) {
        return new BrandSearchParams(statuses, searchField, searchWord, searchParams);
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
