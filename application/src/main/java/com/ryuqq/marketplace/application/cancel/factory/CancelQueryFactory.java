package com.ryuqq.marketplace.application.cancel.factory;

import com.ryuqq.marketplace.application.cancel.dto.query.CancelSearchParams;
import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.domain.cancel.query.CancelDateField;
import com.ryuqq.marketplace.domain.cancel.query.CancelSearchCriteria;
import com.ryuqq.marketplace.domain.cancel.query.CancelSearchField;
import com.ryuqq.marketplace.domain.cancel.query.CancelSortKey;
import com.ryuqq.marketplace.domain.cancel.vo.CancelStatus;
import com.ryuqq.marketplace.domain.cancel.vo.CancelType;
import com.ryuqq.marketplace.domain.common.vo.DateRange;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Component;

/** Cancel 검색 조건 생성 팩토리. */
@Component
public class CancelQueryFactory {

    private final CommonVoFactory commonVoFactory;

    public CancelQueryFactory(CommonVoFactory commonVoFactory) {
        this.commonVoFactory = commonVoFactory;
    }

    public CancelSearchCriteria createCriteria(CancelSearchParams params) {
        List<CancelStatus> statuses = parseStatuses(params.statuses());
        List<CancelType> types = parseTypes(params.types());
        CancelSearchField searchField = parseSearchField(params.searchField());
        CancelDateField dateField = parseDateField(params.dateField());
        CancelSortKey sortKey = parseSortKey(params.sortKey());
        SortDirection sortDirection = parseSortDirection(params.sortDirection());
        DateRange dateRange = parseDateRange(params.startDate(), params.endDate());
        PageRequest pageRequest = commonVoFactory.createPageRequest(params.page(), params.size());
        QueryContext<CancelSortKey> queryContext =
                QueryContext.of(sortKey, sortDirection, pageRequest, false);

        return new CancelSearchCriteria(
                statuses,
                types,
                searchField,
                params.searchWord(),
                dateRange,
                dateField,
                queryContext);
    }

    private List<CancelStatus> parseStatuses(List<String> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return List.of();
        }
        return statuses.stream().map(CancelStatus::valueOf).toList();
    }

    private List<CancelType> parseTypes(List<String> types) {
        if (types == null || types.isEmpty()) {
            return List.of();
        }
        return types.stream().map(CancelType::valueOf).toList();
    }

    private CancelSearchField parseSearchField(String searchField) {
        if (searchField == null || searchField.isBlank()) {
            return null;
        }
        return CancelSearchField.valueOf(searchField);
    }

    private CancelDateField parseDateField(String dateField) {
        if (dateField == null || dateField.isBlank()) {
            return null;
        }
        return CancelDateField.valueOf(dateField);
    }

    private CancelSortKey parseSortKey(String sortKey) {
        if (sortKey == null || sortKey.isBlank()) {
            return CancelSortKey.CREATED_AT;
        }
        return CancelSortKey.valueOf(sortKey);
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
