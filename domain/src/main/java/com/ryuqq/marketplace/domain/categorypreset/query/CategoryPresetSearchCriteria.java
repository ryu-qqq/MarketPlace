package com.ryuqq.marketplace.domain.categorypreset.query;

import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/** CategoryPreset 검색 조건. */
public record CategoryPresetSearchCriteria(
        List<Long> salesChannelIds,
        List<String> statuses,
        String searchField,
        String searchWord,
        LocalDate startDate,
        LocalDate endDate,
        QueryContext<CategoryPresetSortKey> queryContext) {

    public CategoryPresetSearchCriteria {
        Objects.requireNonNull(queryContext, "queryContext must not be null");
    }

    public boolean hasSalesChannelFilter() {
        return salesChannelIds != null && !salesChannelIds.isEmpty();
    }

    public boolean hasStatusFilter() {
        return statuses != null && !statuses.isEmpty();
    }

    public boolean hasSearchFilter() {
        return searchField != null
                && !searchField.isBlank()
                && searchWord != null
                && !searchWord.isBlank();
    }

    public boolean hasStartDateFilter() {
        return startDate != null;
    }

    public boolean hasEndDateFilter() {
        return endDate != null;
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
