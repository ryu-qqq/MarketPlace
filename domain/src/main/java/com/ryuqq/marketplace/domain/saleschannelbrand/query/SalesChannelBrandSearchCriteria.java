package com.ryuqq.marketplace.domain.saleschannelbrand.query;

import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.saleschannelbrand.vo.SalesChannelBrandStatus;
import java.util.List;
import java.util.Objects;

public record SalesChannelBrandSearchCriteria(
        List<Long> salesChannelIds,
        List<SalesChannelBrandStatus> statuses,
        SalesChannelBrandSearchField searchField,
        String searchWord,
        QueryContext<SalesChannelBrandSortKey> queryContext) {

    public SalesChannelBrandSearchCriteria {
        salesChannelIds = salesChannelIds != null ? List.copyOf(salesChannelIds) : List.of();
        statuses = statuses != null ? List.copyOf(statuses) : List.of();
        Objects.requireNonNull(queryContext, "queryContext must not be null");
    }

    public static SalesChannelBrandSearchCriteria of(
            List<Long> salesChannelIds,
            List<SalesChannelBrandStatus> statuses,
            SalesChannelBrandSearchField searchField,
            String searchWord,
            QueryContext<SalesChannelBrandSortKey> queryContext) {
        return new SalesChannelBrandSearchCriteria(
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
