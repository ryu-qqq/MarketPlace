package com.ryuqq.marketplace.application.productgroup.dto.query;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import java.util.List;

/** 상품 그룹 검색 파라미터 DTO. */
public record ProductGroupSearchParams(
        List<String> statuses,
        List<Long> sellerIds,
        List<Long> brandIds,
        List<Long> categoryIds,
        List<Long> productGroupIds,
        String searchField,
        String searchWord,
        CommonSearchParams searchParams) {

    public static ProductGroupSearchParams of(
            List<String> statuses,
            List<Long> sellerIds,
            List<Long> brandIds,
            List<Long> categoryIds,
            List<Long> productGroupIds,
            String searchField,
            String searchWord,
            CommonSearchParams searchParams) {
        return new ProductGroupSearchParams(
                statuses,
                sellerIds,
                brandIds,
                categoryIds,
                productGroupIds,
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

    public java.time.LocalDate startDate() {
        return searchParams.startDate();
    }

    public java.time.LocalDate endDate() {
        return searchParams.endDate();
    }

    public ProductGroupSearchParams withCategoryIds(List<Long> expandedCategoryIds) {
        return new ProductGroupSearchParams(
                statuses,
                sellerIds,
                brandIds,
                expandedCategoryIds,
                productGroupIds,
                searchField,
                searchWord,
                searchParams);
    }
}
