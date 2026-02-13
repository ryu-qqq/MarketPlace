package com.ryuqq.marketplace.domain.cancel.query;

import com.ryuqq.marketplace.domain.cancel.vo.CancelStatus;
import com.ryuqq.marketplace.domain.cancel.vo.CancelType;
import com.ryuqq.marketplace.domain.common.vo.DateRange;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import java.util.List;

/** 취소 검색 조건 Criteria. */
public record CancelSearchCriteria(
        List<CancelStatus> statuses,
        List<CancelType> types,
        CancelSearchField searchField,
        String searchWord,
        DateRange dateRange,
        CancelDateField dateField,
        QueryContext<CancelSortKey> queryContext) {

    public CancelSearchCriteria {
        statuses = statuses != null ? List.copyOf(statuses) : List.of();
        types = types != null ? List.copyOf(types) : List.of();
    }

    public static CancelSearchCriteria of(
            List<CancelStatus> statuses,
            List<CancelType> types,
            CancelSearchField searchField,
            String searchWord,
            DateRange dateRange,
            CancelDateField dateField,
            QueryContext<CancelSortKey> queryContext) {
        return new CancelSearchCriteria(
                statuses, types, searchField, searchWord, dateRange, dateField, queryContext);
    }

    public static CancelSearchCriteria defaultCriteria() {
        return new CancelSearchCriteria(
                List.of(),
                List.of(),
                null,
                null,
                null,
                null,
                QueryContext.defaultOf(CancelSortKey.defaultKey()));
    }

    public boolean hasStatusFilter() {
        return !statuses.isEmpty();
    }

    public boolean hasTypeFilter() {
        return !types.isEmpty();
    }

    public boolean hasSearchCondition() {
        return searchWord != null && !searchWord.isBlank();
    }

    public boolean hasSearchField() {
        return searchField != null;
    }

    public boolean hasDateRange() {
        return dateRange != null && !dateRange.isEmpty();
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
