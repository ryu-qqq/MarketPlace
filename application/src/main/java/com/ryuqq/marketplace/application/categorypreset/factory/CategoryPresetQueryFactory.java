package com.ryuqq.marketplace.application.categorypreset.factory;

import com.ryuqq.marketplace.application.categorypreset.dto.query.CategoryPresetSearchParams;
import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.domain.categorypreset.query.CategoryPresetSearchCriteria;
import com.ryuqq.marketplace.domain.categorypreset.query.CategoryPresetSortKey;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import org.springframework.stereotype.Component;

/** CategoryPreset Query Factory. */
@Component
public class CategoryPresetQueryFactory {

    private final CommonVoFactory commonVoFactory;

    public CategoryPresetQueryFactory(CommonVoFactory commonVoFactory) {
        this.commonVoFactory = commonVoFactory;
    }

    public CategoryPresetSearchCriteria createCriteria(CategoryPresetSearchParams params) {
        CategoryPresetSortKey sortKey = resolveSortKey(params.commonSearchParams().sortKey());
        SortDirection sortDirection =
                commonVoFactory.parseSortDirection(params.commonSearchParams().sortDirection());
        PageRequest pageRequest =
                commonVoFactory.createPageRequest(
                        params.commonSearchParams().page(), params.commonSearchParams().size());

        QueryContext<CategoryPresetSortKey> queryContext =
                commonVoFactory.createQueryContext(
                        sortKey,
                        sortDirection,
                        pageRequest,
                        params.commonSearchParams().includeDeleted());

        return new CategoryPresetSearchCriteria(
                params.salesChannelIds(),
                params.statuses(),
                params.searchField(),
                params.searchWord(),
                params.commonSearchParams().startDate(),
                params.commonSearchParams().endDate(),
                queryContext);
    }

    private CategoryPresetSortKey resolveSortKey(String sortKeyString) {
        if (sortKeyString == null || sortKeyString.isBlank()) {
            return CategoryPresetSortKey.defaultKey();
        }
        for (CategoryPresetSortKey key : CategoryPresetSortKey.values()) {
            if (key.fieldName().equalsIgnoreCase(sortKeyString)
                    || key.name().equalsIgnoreCase(sortKeyString)) {
                return key;
            }
        }
        return CategoryPresetSortKey.defaultKey();
    }
}
