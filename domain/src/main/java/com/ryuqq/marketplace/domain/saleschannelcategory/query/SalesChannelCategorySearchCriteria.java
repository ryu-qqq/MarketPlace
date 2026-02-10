package com.ryuqq.marketplace.domain.saleschannelcategory.query;

import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.saleschannelcategory.vo.SalesChannelCategoryStatus;
import java.util.List;
import java.util.Objects;

public record SalesChannelCategorySearchCriteria(
        List<Long> salesChannelIds,
        List<SalesChannelCategoryStatus> statuses,
        SalesChannelCategorySearchField searchField,
        String searchWord,
        QueryContext<SalesChannelCategorySortKey> queryContext) {

    public SalesChannelCategorySearchCriteria {
        salesChannelIds = salesChannelIds != null ? List.copyOf(salesChannelIds) : List.of();
        statuses = statuses != null ? List.copyOf(statuses) : List.of();
        Objects.requireNonNull(queryContext, "queryContext must not be null");
    }

    public static SalesChannelCategorySearchCriteria of(
            List<Long> salesChannelIds,
            List<SalesChannelCategoryStatus> statuses,
            SalesChannelCategorySearchField searchField,
            String searchWord,
            QueryContext<SalesChannelCategorySortKey> queryContext) {
        return new SalesChannelCategorySearchCriteria(
                salesChannelIds, statuses, searchField, searchWord, queryContext);
    }

    public boolean hasSalesChannelFilter() {
        return !salesChannelIds.isEmpty();
    }

    public boolean hasStatusFilter() {
        return !statuses.isEmpty();
    }

    public boolean hasSearchCondition() {
        return searchWord != null && !searchWord.isBlank();
    }

    public boolean hasSearchField() {
        return searchField != null;
    }

    public int size() {
        return queryContext.size();
    }

    public long offset() {
        return queryContext.offset();
    }

    public int page() {
        return queryContext.page();
    }
}
