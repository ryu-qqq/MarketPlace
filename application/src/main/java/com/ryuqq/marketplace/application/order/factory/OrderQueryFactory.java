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
import java.util.ArrayList;
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
        OrderSortKey sortKey = OrderSortKey.fromString(params.searchParams().sortKey());
        SortDirection sortDirection = commonVoFactory.parseSortDirection(params.searchParams().sortDirection());
        PageRequest pageRequest = commonVoFactory.createPageRequest(params.searchParams().page(), params.searchParams().size());

        QueryContext<OrderSortKey> queryContext =
                commonVoFactory.createQueryContext(
                        sortKey,
                        sortDirection,
                        pageRequest,
                        params.searchParams().includeDeleted());

        List<OrderItemStatus> statuses = new ArrayList<>();
        List<String> crossDomainStatuses = new ArrayList<>();
        classifyStatuses(params.statuses(), statuses, crossDomainStatuses);

        OrderSearchField searchField = OrderSearchField.fromString(params.searchField());
        OrderDateField dateField = OrderDateField.fromString(params.dateField());
        DateRange dateRange =
                commonVoFactory.createDateRange(
                        params.searchParams().startDate(), params.searchParams().endDate());

        return OrderSearchCriteria.of(
                statuses,
                crossDomainStatuses,
                params.shopId(),
                searchField,
                params.searchWord(),
                dateRange,
                dateField,
                queryContext);
    }

    /**
     * 프론트 상태 문자열을 OrderItemStatus와 crossDomain 상태로 분류.
     *
     * <p>OrderItemStatus.valueOf()로 변환 가능하면 statuses에, 실패하면 crossDomainStatuses에 추가합니다.
     */
    private void classifyStatuses(
            List<String> rawStatuses,
            List<OrderItemStatus> statuses,
            List<String> crossDomainStatuses) {
        if (rawStatuses == null || rawStatuses.isEmpty()) {
            return;
        }
        for (String raw : rawStatuses) {
            if (raw == null || raw.isBlank()) {
                continue;
            }
            String upper = raw.trim().toUpperCase(Locale.ROOT);
            try {
                statuses.add(OrderItemStatus.valueOf(upper));
            } catch (IllegalArgumentException e) {
                crossDomainStatuses.add(upper);
            }
        }
    }
}
