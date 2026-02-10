package com.ryuqq.marketplace.application.saleschannelcategory.factory;

import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.application.saleschannelcategory.dto.query.SalesChannelCategorySearchParams;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.saleschannelcategory.query.SalesChannelCategorySearchCriteria;
import com.ryuqq.marketplace.domain.saleschannelcategory.query.SalesChannelCategorySearchField;
import com.ryuqq.marketplace.domain.saleschannelcategory.query.SalesChannelCategorySortKey;
import com.ryuqq.marketplace.domain.saleschannelcategory.vo.SalesChannelCategoryStatus;
import java.util.List;
import org.springframework.stereotype.Component;

/** SalesChannelCategory Query Factory. */
@Component
public class SalesChannelCategoryQueryFactory {

    private final CommonVoFactory commonVoFactory;

    public SalesChannelCategoryQueryFactory(CommonVoFactory commonVoFactory) {
        this.commonVoFactory = commonVoFactory;
    }

    public SalesChannelCategorySearchCriteria createCriteria(
            SalesChannelCategorySearchParams params) {
        SalesChannelCategorySortKey sortKey = resolveSortKey(params.sortKey());
        SortDirection sortDirection = commonVoFactory.parseSortDirection(params.sortDirection());
        PageRequest pageRequest = commonVoFactory.createPageRequest(params.page(), params.size());

        QueryContext<SalesChannelCategorySortKey> queryContext =
                commonVoFactory.createQueryContext(
                        sortKey,
                        sortDirection,
                        pageRequest,
                        params.searchParams().includeDeleted());

        SalesChannelCategorySearchField searchField =
                SalesChannelCategorySearchField.fromString(params.searchField());

        List<SalesChannelCategoryStatus> statuses =
                params.statuses() != null
                        ? params.statuses().stream()
                                .map(SalesChannelCategoryStatus::fromString)
                                .toList()
                        : List.of();

        return SalesChannelCategorySearchCriteria.of(
                params.salesChannelIds(), statuses, searchField, params.searchWord(), queryContext);
    }

    private SalesChannelCategorySortKey resolveSortKey(String sortKeyString) {
        if (sortKeyString == null || sortKeyString.isBlank()) {
            return SalesChannelCategorySortKey.defaultKey();
        }
        for (SalesChannelCategorySortKey key : SalesChannelCategorySortKey.values()) {
            if (key.fieldName().equalsIgnoreCase(sortKeyString)
                    || key.name().equalsIgnoreCase(sortKeyString)) {
                return key;
            }
        }
        return SalesChannelCategorySortKey.defaultKey();
    }
}
