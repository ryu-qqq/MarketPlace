package com.ryuqq.marketplace.application.categorymapping.factory;

import com.ryuqq.marketplace.application.categorymapping.dto.query.CategoryMappingSearchParams;
import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.domain.categorymapping.query.CategoryMappingSearchCriteria;
import com.ryuqq.marketplace.domain.categorymapping.query.CategoryMappingSearchField;
import com.ryuqq.marketplace.domain.categorymapping.query.CategoryMappingSortKey;
import com.ryuqq.marketplace.domain.categorymapping.vo.CategoryMappingStatus;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import java.util.List;
import org.springframework.stereotype.Component;

/** CategoryMapping Query Factory. */
@Component
public class CategoryMappingQueryFactory {

    private final CommonVoFactory commonVoFactory;

    public CategoryMappingQueryFactory(CommonVoFactory commonVoFactory) {
        this.commonVoFactory = commonVoFactory;
    }

    public CategoryMappingSearchCriteria createCriteria(CategoryMappingSearchParams params) {
        CategoryMappingSortKey sortKey = resolveSortKey(params.sortKey());
        SortDirection sortDirection = commonVoFactory.parseSortDirection(params.sortDirection());
        PageRequest pageRequest = commonVoFactory.createPageRequest(params.page(), params.size());

        QueryContext<CategoryMappingSortKey> queryContext =
                commonVoFactory.createQueryContext(sortKey, sortDirection, pageRequest);

        List<CategoryMappingStatus> statuses =
                params.statuses() != null
                        ? params.statuses().stream().map(CategoryMappingStatus::fromString).toList()
                        : List.of();

        CategoryMappingSearchField searchField =
                CategoryMappingSearchField.fromString(params.searchField());

        return CategoryMappingSearchCriteria.of(
                params.salesChannelCategoryIds(),
                params.internalCategoryIds(),
                params.salesChannelIds(),
                statuses,
                searchField,
                params.searchWord(),
                queryContext);
    }

    private CategoryMappingSortKey resolveSortKey(String sortKeyString) {
        if (sortKeyString == null || sortKeyString.isBlank()) {
            return CategoryMappingSortKey.defaultKey();
        }
        for (CategoryMappingSortKey key : CategoryMappingSortKey.values()) {
            if (key.fieldName().equalsIgnoreCase(sortKeyString)
                    || key.name().equalsIgnoreCase(sortKeyString)) {
                return key;
            }
        }
        return CategoryMappingSortKey.defaultKey();
    }
}
