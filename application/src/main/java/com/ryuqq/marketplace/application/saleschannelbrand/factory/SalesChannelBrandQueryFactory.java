package com.ryuqq.marketplace.application.saleschannelbrand.factory;

import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.application.saleschannelbrand.dto.query.SalesChannelBrandSearchParams;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.saleschannelbrand.query.SalesChannelBrandSearchCriteria;
import com.ryuqq.marketplace.domain.saleschannelbrand.query.SalesChannelBrandSearchField;
import com.ryuqq.marketplace.domain.saleschannelbrand.query.SalesChannelBrandSortKey;
import com.ryuqq.marketplace.domain.saleschannelbrand.vo.SalesChannelBrandStatus;
import java.util.List;
import org.springframework.stereotype.Component;

/** SalesChannelBrand Query Factory. */
@Component
public class SalesChannelBrandQueryFactory {

    private final CommonVoFactory commonVoFactory;

    public SalesChannelBrandQueryFactory(CommonVoFactory commonVoFactory) {
        this.commonVoFactory = commonVoFactory;
    }

    public SalesChannelBrandSearchCriteria createCriteria(SalesChannelBrandSearchParams params) {
        SalesChannelBrandSortKey sortKey = resolveSortKey(params.sortKey());
        SortDirection sortDirection = commonVoFactory.parseSortDirection(params.sortDirection());
        PageRequest pageRequest = commonVoFactory.createPageRequest(params.page(), params.size());

        QueryContext<SalesChannelBrandSortKey> queryContext =
                commonVoFactory.createQueryContext(
                        sortKey,
                        sortDirection,
                        pageRequest,
                        params.searchParams().includeDeleted());

        SalesChannelBrandSearchField searchField =
                SalesChannelBrandSearchField.fromString(params.searchField());

        List<SalesChannelBrandStatus> statuses =
                params.statuses() != null
                        ? params.statuses().stream()
                                .map(SalesChannelBrandStatus::fromString)
                                .toList()
                        : List.of();

        return SalesChannelBrandSearchCriteria.of(
                params.salesChannelIds(), statuses, searchField, params.searchWord(), queryContext);
    }

    private SalesChannelBrandSortKey resolveSortKey(String sortKeyString) {
        if (sortKeyString == null || sortKeyString.isBlank()) {
            return SalesChannelBrandSortKey.defaultKey();
        }
        for (SalesChannelBrandSortKey key : SalesChannelBrandSortKey.values()) {
            if (key.fieldName().equalsIgnoreCase(sortKeyString)
                    || key.name().equalsIgnoreCase(sortKeyString)) {
                return key;
            }
        }
        return SalesChannelBrandSortKey.defaultKey();
    }
}
