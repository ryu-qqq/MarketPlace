package com.ryuqq.marketplace.application.exchange.factory;

import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.application.exchange.dto.query.ExchangeSearchParams;
import com.ryuqq.marketplace.domain.common.vo.DateRange;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.exchange.query.ExchangeDateField;
import com.ryuqq.marketplace.domain.exchange.query.ExchangeSearchCriteria;
import com.ryuqq.marketplace.domain.exchange.query.ExchangeSearchField;
import com.ryuqq.marketplace.domain.exchange.query.ExchangeSortKey;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeStatus;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Component;

/** Exchange 검색 조건 생성 팩토리. */
@Component
public class ExchangeQueryFactory {

    private final CommonVoFactory commonVoFactory;

    public ExchangeQueryFactory(CommonVoFactory commonVoFactory) {
        this.commonVoFactory = commonVoFactory;
    }

    public ExchangeSearchCriteria createCriteria(ExchangeSearchParams params) {
        List<ExchangeStatus> statuses = ExchangeStatus.fromStringList(params.statuses());
        ExchangeSearchField searchField = ExchangeSearchField.fromString(params.searchField());
        ExchangeDateField dateField = ExchangeDateField.fromString(params.dateField());
        ExchangeSortKey sortKey = ExchangeSortKey.fromString(params.sortKey());
        SortDirection sortDirection = commonVoFactory.parseSortDirection(params.sortDirection());
        DateRange dateRange = parseDateRange(params.startDate(), params.endDate());
        PageRequest pageRequest = commonVoFactory.createPageRequest(params.page(), params.size());
        QueryContext<ExchangeSortKey> queryContext =
                QueryContext.of(sortKey, sortDirection, pageRequest, false);

        return new ExchangeSearchCriteria(
                statuses, searchField, params.searchWord(), dateRange, dateField, queryContext);
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
