package com.ryuqq.marketplace.application.categorypreset.factory;

import com.ryuqq.marketplace.application.categorypreset.dto.query.CategoryPresetSearchParams;
import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.domain.categorypreset.query.CategoryPresetSearchCriteria;
import com.ryuqq.marketplace.domain.categorypreset.query.CategoryPresetSortKey;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

/** CategoryPreset Query Factory. */
@Component
public class CategoryPresetQueryFactory {

    private final CommonVoFactory commonVoFactory;

    public CategoryPresetQueryFactory(CommonVoFactory commonVoFactory) {
        this.commonVoFactory = commonVoFactory;
    }

    public CategoryPresetSearchCriteria createCriteria(CategoryPresetSearchParams params) {
        PageRequest pageRequest = commonVoFactory.createPageRequest(params.page(), params.size());
        SortDirection sortDirection =
                params.sortDirection() != null
                        ? commonVoFactory.parseSortDirection(params.sortDirection())
                        : SortDirection.DESC;
        QueryContext<CategoryPresetSortKey> queryContext =
                commonVoFactory.createQueryContext(
                        CategoryPresetSortKey.defaultKey(), sortDirection, pageRequest);

        LocalDate startDate =
                params.startDate() != null ? LocalDate.parse(params.startDate()) : null;
        LocalDate endDate = params.endDate() != null ? LocalDate.parse(params.endDate()) : null;

        return new CategoryPresetSearchCriteria(
                params.salesChannelIds(),
                params.statuses(),
                params.searchField(),
                params.searchWord(),
                startDate,
                endDate,
                queryContext);
    }
}
