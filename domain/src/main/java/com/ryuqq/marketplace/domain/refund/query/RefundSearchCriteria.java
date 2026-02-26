package com.ryuqq.marketplace.domain.refund.query;

import com.ryuqq.marketplace.domain.common.vo.DateRange;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.refund.vo.RefundStatus;
import java.util.List;

/** 환불 검색 조건 Criteria. */
public record RefundSearchCriteria(
        List<RefundStatus> statuses,
        Boolean isHold,
        RefundSearchField searchField,
        String searchWord,
        DateRange dateRange,
        RefundDateField dateField,
        QueryContext<RefundSortKey> queryContext) {

    public RefundSearchCriteria {
        statuses = statuses != null ? List.copyOf(statuses) : List.of();
    }

    public static RefundSearchCriteria of(
            List<RefundStatus> statuses,
            Boolean isHold,
            RefundSearchField searchField,
            String searchWord,
            DateRange dateRange,
            RefundDateField dateField,
            QueryContext<RefundSortKey> queryContext) {
        return new RefundSearchCriteria(
                statuses, isHold, searchField, searchWord, dateRange, dateField, queryContext);
    }

    public static RefundSearchCriteria defaultCriteria() {
        return new RefundSearchCriteria(
                List.of(),
                null,
                null,
                null,
                null,
                null,
                QueryContext.defaultOf(RefundSortKey.defaultKey()));
    }

    public boolean hasStatusFilter() {
        return !statuses.isEmpty();
    }

    public boolean hasHoldFilter() {
        return isHold != null;
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
