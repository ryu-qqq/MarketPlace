package com.ryuqq.marketplace.domain.order.query;

import com.ryuqq.marketplace.domain.common.vo.DateRange;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.order.vo.OrderItemStatus;
import java.util.List;

/** 주문 검색 조건 Criteria. */
public record OrderSearchCriteria(
        List<OrderItemStatus> statuses,
        List<String> crossDomainStatuses,
        Long shopId,
        OrderSearchField searchField,
        String searchWord,
        DateRange dateRange,
        OrderDateField dateField,
        QueryContext<OrderSortKey> queryContext) {

    public OrderSearchCriteria {
        statuses = statuses != null ? List.copyOf(statuses) : List.of();
        crossDomainStatuses =
                crossDomainStatuses != null ? List.copyOf(crossDomainStatuses) : List.of();
    }

    public static OrderSearchCriteria of(
            List<OrderItemStatus> statuses,
            List<String> crossDomainStatuses,
            Long shopId,
            OrderSearchField searchField,
            String searchWord,
            DateRange dateRange,
            OrderDateField dateField,
            QueryContext<OrderSortKey> queryContext) {
        return new OrderSearchCriteria(
                statuses,
                crossDomainStatuses,
                shopId,
                searchField,
                searchWord,
                dateRange,
                dateField,
                queryContext);
    }

    public static OrderSearchCriteria defaultCriteria() {
        return new OrderSearchCriteria(
                List.of(),
                List.of(),
                null,
                null,
                null,
                null,
                null,
                QueryContext.defaultOf(OrderSortKey.defaultKey()));
    }

    public boolean hasStatusFilter() {
        return !statuses.isEmpty();
    }

    public boolean hasCrossDomainStatusFilter() {
        return !crossDomainStatuses.isEmpty();
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
