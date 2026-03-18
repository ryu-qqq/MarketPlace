package com.ryuqq.marketplace.application.refund.factory;

import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.application.refund.dto.query.RefundSearchParams;
import com.ryuqq.marketplace.domain.common.vo.DateRange;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.refund.query.RefundDateField;
import com.ryuqq.marketplace.domain.refund.query.RefundSearchCriteria;
import com.ryuqq.marketplace.domain.refund.query.RefundSearchField;
import com.ryuqq.marketplace.domain.refund.query.RefundSortKey;
import com.ryuqq.marketplace.domain.refund.vo.RefundStatus;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Component;

/** Refund 검색 조건 생성 팩토리. */
@Component
public class RefundQueryFactory {

    private final CommonVoFactory commonVoFactory;

    public RefundQueryFactory(CommonVoFactory commonVoFactory) {
        this.commonVoFactory = commonVoFactory;
    }

    public RefundSearchCriteria createCriteria(RefundSearchParams params) {
        List<RefundStatus> statuses = parseStatuses(params.statuses());
        RefundSearchField searchField = parseSearchField(params.searchField());
        RefundDateField dateField = parseDateField(params.dateField());
        RefundSortKey sortKey = parseSortKey(params.sortKey());
        SortDirection sortDirection = parseSortDirection(params.sortDirection());
        DateRange dateRange = parseDateRange(params.startDate(), params.endDate());
        PageRequest pageRequest = commonVoFactory.createPageRequest(params.page(), params.size());
        QueryContext<RefundSortKey> queryContext =
                QueryContext.of(sortKey, sortDirection, pageRequest, false);

        return new RefundSearchCriteria(
                statuses, null, searchField, params.searchWord(), dateRange, dateField, queryContext);
    }

    private List<RefundStatus> parseStatuses(List<String> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return List.of();
        }
        return statuses.stream().map(RefundStatus::valueOf).toList();
    }

    private RefundSearchField parseSearchField(String searchField) {
        if (searchField == null || searchField.isBlank()) {
            return null;
        }
        return RefundSearchField.valueOf(searchField);
    }

    private RefundDateField parseDateField(String dateField) {
        if (dateField == null || dateField.isBlank()) {
            return null;
        }
        return RefundDateField.valueOf(dateField);
    }

    private RefundSortKey parseSortKey(String sortKey) {
        if (sortKey == null || sortKey.isBlank()) {
            return RefundSortKey.CREATED_AT;
        }
        return RefundSortKey.valueOf(sortKey);
    }

    private SortDirection parseSortDirection(String direction) {
        if (direction == null || direction.isBlank()) {
            return SortDirection.DESC;
        }
        return SortDirection.valueOf(direction);
    }

    private DateRange parseDateRange(String startDate, String endDate) {
        if (startDate == null && endDate == null) {
            return null;
        }
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : null;
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : null;
        return commonVoFactory.createDateRange(start, end);
    }
}
