package com.ryuqq.marketplace.application.saleschannel.factory;

import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.application.saleschannel.dto.query.SalesChannelSearchParams;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.saleschannel.query.SalesChannelSearchCriteria;
import com.ryuqq.marketplace.domain.saleschannel.query.SalesChannelSearchField;
import com.ryuqq.marketplace.domain.saleschannel.query.SalesChannelSortKey;
import com.ryuqq.marketplace.domain.saleschannel.vo.SalesChannelStatus;
import java.util.List;
import org.springframework.stereotype.Component;

/** SalesChannel Query Factory. */
@Component
public class SalesChannelQueryFactory {

    private final CommonVoFactory commonVoFactory;

    public SalesChannelQueryFactory(CommonVoFactory commonVoFactory) {
        this.commonVoFactory = commonVoFactory;
    }

    public SalesChannelSearchCriteria createCriteria(SalesChannelSearchParams params) {
        SalesChannelSortKey sortKey = resolveSortKey(params.sortKey());
        SortDirection sortDirection = commonVoFactory.parseSortDirection(params.sortDirection());
        PageRequest pageRequest = commonVoFactory.createPageRequest(params.page(), params.size());

        QueryContext<SalesChannelSortKey> queryContext =
                commonVoFactory.createQueryContext(
                        sortKey,
                        sortDirection,
                        pageRequest,
                        params.searchParams().includeDeleted());

        SalesChannelSearchField searchField =
                SalesChannelSearchField.fromString(params.searchField());

        List<SalesChannelStatus> statuses =
                params.statuses() != null
                        ? params.statuses().stream().map(SalesChannelStatus::fromString).toList()
                        : List.of();

        return SalesChannelSearchCriteria.of(
                statuses, searchField, params.searchWord(), queryContext);
    }

    private SalesChannelSortKey resolveSortKey(String sortKeyString) {
        if (sortKeyString == null || sortKeyString.isBlank()) {
            return SalesChannelSortKey.defaultKey();
        }
        for (SalesChannelSortKey key : SalesChannelSortKey.values()) {
            if (key.fieldName().equalsIgnoreCase(sortKeyString)
                    || key.name().equalsIgnoreCase(sortKeyString)) {
                return key;
            }
        }
        return SalesChannelSortKey.defaultKey();
    }
}
