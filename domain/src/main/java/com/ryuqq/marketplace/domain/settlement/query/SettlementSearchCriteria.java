package com.ryuqq.marketplace.domain.settlement.query;

import com.ryuqq.marketplace.domain.common.vo.DateRange;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.settlement.vo.SettlementStatus;
import java.util.List;

/** 정산 검색 조건 Criteria. */
public record SettlementSearchCriteria(
        List<SettlementStatus> statuses,
        List<Long> sellerIds,
        SettlementSearchField searchField,
        String searchWord,
        DateRange dateRange,
        SettlementDateField dateField,
        QueryContext<SettlementSortKey> queryContext) {

    public SettlementSearchCriteria {
        statuses = statuses != null ? List.copyOf(statuses) : List.of();
        sellerIds = sellerIds != null ? List.copyOf(sellerIds) : List.of();
    }

    public static SettlementSearchCriteria of(
            List<SettlementStatus> statuses,
            List<Long> sellerIds,
            SettlementSearchField searchField,
            String searchWord,
            DateRange dateRange,
            SettlementDateField dateField,
            QueryContext<SettlementSortKey> queryContext) {
        return new SettlementSearchCriteria(
                statuses, sellerIds, searchField, searchWord, dateRange, dateField, queryContext);
    }

    public static SettlementSearchCriteria defaultCriteria() {
        return new SettlementSearchCriteria(
                List.of(),
                List.of(),
                null,
                null,
                null,
                null,
                QueryContext.defaultOf(SettlementSortKey.defaultKey()));
    }

    public boolean hasStatusFilter() {
        return !statuses.isEmpty();
    }

    public boolean hasSellerFilter() {
        return !sellerIds.isEmpty();
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
