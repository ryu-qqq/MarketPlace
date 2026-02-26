package com.ryuqq.marketplace.domain.exchange.query;

import com.ryuqq.marketplace.domain.common.vo.DateRange;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeStatus;
import java.util.List;

/** 교환 검색 조건 Criteria. */
public record ExchangeSearchCriteria(
        List<ExchangeStatus> statuses,
        ExchangeSearchField searchField,
        String searchWord,
        DateRange dateRange,
        ExchangeDateField dateField,
        QueryContext<ExchangeSortKey> queryContext) {

    public ExchangeSearchCriteria {
        statuses = statuses != null ? List.copyOf(statuses) : List.of();
    }

    public static ExchangeSearchCriteria of(
            List<ExchangeStatus> statuses,
            ExchangeSearchField searchField,
            String searchWord,
            DateRange dateRange,
            ExchangeDateField dateField,
            QueryContext<ExchangeSortKey> queryContext) {
        return new ExchangeSearchCriteria(
                statuses, searchField, searchWord, dateRange, dateField, queryContext);
    }

    public static ExchangeSearchCriteria defaultCriteria() {
        return new ExchangeSearchCriteria(
                List.of(),
                null,
                null,
                null,
                null,
                QueryContext.defaultOf(ExchangeSortKey.defaultKey()));
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
