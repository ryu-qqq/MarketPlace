package com.ryuqq.marketplace.application.category.dto.query;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import java.util.List;

/** 카테고리 검색 파라미터 DTO. */
public record CategorySearchParams(
        Long parentId,
        Integer depth,
        Boolean leaf,
        List<String> statuses,
        List<String> departments,
        List<String> categoryGroups,
        String searchField,
        String searchWord,
        CommonSearchParams searchParams) {

    public static CategorySearchParams of(
            Long parentId,
            Integer depth,
            Boolean leaf,
            List<String> statuses,
            List<String> departments,
            List<String> categoryGroups,
            String searchField,
            String searchWord,
            CommonSearchParams searchParams) {
        return new CategorySearchParams(
                parentId,
                depth,
                leaf,
                statuses,
                departments,
                categoryGroups,
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
