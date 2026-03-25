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
        List<CancelStatus> statuses = CancelStatus.fromStringList(params.statuses());
        List<CancelType> types = CancelType.fromStringList(params.types());
        CancelSearchField searchField = CancelSearchField.fromString(params.searchField());
        CancelDateField dateField = CancelDateField.fromString(params.dateField());
        CancelSortKey sortKey = CancelSortKey.fromString(params.sortKey());
        SortDirection sortDirection = commonVoFactory.parseSortDirection(params.sortDirection());
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

    private DateRange parseDateRange(String startDate, String endDate) {
        if (startDate == null && endDate == null) {
            return null;
        }
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : null;
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : null;
        return commonVoFactory.createDateRange(start, end);
    }
}
