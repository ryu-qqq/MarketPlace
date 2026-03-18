package com.ryuqq.marketplace.application.order.factory;

import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.application.order.dto.query.OrderSearchParams;
import com.ryuqq.marketplace.domain.common.vo.DateRange;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.order.query.OrderDateField;
import com.ryuqq.marketplace.domain.order.query.OrderSearchCriteria;
import com.ryuqq.marketplace.domain.order.query.OrderSearchField;
import com.ryuqq.marketplace.domain.order.query.OrderSortKey;
import com.ryuqq.marketplace.domain.order.vo.OrderItemStatus;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

/**
 * Order Query Factory.
 *
 * <p>Query DTO를 Domain Criteria로 변환합니다.
 */
@Component
public class OrderQueryFactory {

    private final CommonVoFactory commonVoFactory;

    public OrderQueryFactory(CommonVoFactory commonVoFactory) {
        this.commonVoFactory = commonVoFactory;
    }

    /**
     * OrderSearchParams로부터 OrderSearchCriteria 생성.
     *
     * @param params 검색 파라미터
     * @return OrderSearchCriteria
     */
    public OrderSearchCriteria createCriteria(OrderSearchParams params) {
        OrderSortKey sortKey = resolveSortKey(params.sortKey());
        SortDirection sortDirection = commonVoFactory.parseSortDirection(params.sortDirection());
        PageRequest pageRequest = commonVoFactory.createPageRequest(params.page(), params.size());

        QueryContext<OrderSortKey> queryContext =
                commonVoFactory.createQueryContext(
                        sortKey,
                        sortDirection,
                        pageRequest,
                        params.searchParams().includeDeleted());

        List<OrderItemStatus> statuses = resolveStatuses(params.statuses());
        OrderSearchField searchField = OrderSearchField.fromString(params.searchField());
        OrderDateField dateField = resolveDateField(params.dateField());
        DateRange dateRange =
                commonVoFactory.createDateRange(
                        params.searchParams().startDate(), params.searchParams().endDate());

        return OrderSearchCriteria.of(
                statuses, searchField, params.searchWord(), dateRange, dateField, queryContext);
    }

    private OrderSortKey resolveSortKey(String sortKeyString) {
        if (sortKeyString == null || sortKeyString.isBlank()) {
            return OrderSortKey.defaultKey();
        }

        for (OrderSortKey key : OrderSortKey.values()) {
            if (key.fieldName().equalsIgnoreCase(sortKeyString)
                    || key.name().equalsIgnoreCase(sortKeyString)) {
                return key;
            }
        }

        return OrderSortKey.defaultKey();
    }

    private List<OrderItemStatus> resolveStatuses(List<String> statusStrings) {
        if (statusStrings == null || statusStrings.isEmpty()) {
            return List.of();
        }

        return statusStrings.stream()
                .map(s -> OrderItemStatus.valueOf(s.toUpperCase(Locale.ROOT)))
                .toList();
    }

    private OrderDateField resolveDateField(String dateFieldString) {
        if (dateFieldString == null || dateFieldString.isBlank()) {
            return null;
        }

        for (OrderDateField field : OrderDateField.values()) {
            if (field.name().equalsIgnoreCase(dateFieldString)) {
                return field;
            }
        }

        return null;
    }
}
