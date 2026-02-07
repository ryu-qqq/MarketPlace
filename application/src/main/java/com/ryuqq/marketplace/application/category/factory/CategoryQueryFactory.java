package com.ryuqq.marketplace.application.category.factory;

import com.ryuqq.marketplace.application.category.dto.query.CategorySearchParams;
import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.domain.category.query.CategorySearchCriteria;
import com.ryuqq.marketplace.domain.category.query.CategorySearchField;
import com.ryuqq.marketplace.domain.category.query.CategorySortKey;
import com.ryuqq.marketplace.domain.category.vo.CategoryGroup;
import com.ryuqq.marketplace.domain.category.vo.CategoryStatus;
import com.ryuqq.marketplace.domain.category.vo.Department;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import java.util.List;
import org.springframework.stereotype.Component;

/** Category Query Factory. */
@Component
public class CategoryQueryFactory {

    private final CommonVoFactory commonVoFactory;

    public CategoryQueryFactory(CommonVoFactory commonVoFactory) {
        this.commonVoFactory = commonVoFactory;
    }

    public CategorySearchCriteria createCriteria(CategorySearchParams params) {
        CategorySortKey sortKey = resolveSortKey(params.sortKey());
        SortDirection sortDirection = commonVoFactory.parseSortDirection(params.sortDirection());
        PageRequest pageRequest = commonVoFactory.createPageRequest(params.page(), params.size());

        QueryContext<CategorySortKey> queryContext =
                commonVoFactory.createQueryContext(
                        sortKey,
                        sortDirection,
                        pageRequest,
                        params.searchParams().includeDeleted());

        CategorySearchField searchField = CategorySearchField.fromString(params.searchField());

        List<CategoryStatus> statuses =
                params.statuses() != null
                        ? params.statuses().stream().map(CategoryStatus::fromString).toList()
                        : List.of();

        List<Department> departments =
                params.departments() != null
                        ? params.departments().stream().map(Department::fromString).toList()
                        : List.of();

        List<CategoryGroup> categoryGroups =
                params.categoryGroups() != null
                        ? params.categoryGroups().stream().map(CategoryGroup::fromString).toList()
                        : List.of();

        return CategorySearchCriteria.of(
                params.parentId(),
                params.depth(),
                params.leaf(),
                statuses,
                departments,
                categoryGroups,
                searchField,
                params.searchWord(),
                queryContext);
    }

    private CategorySortKey resolveSortKey(String sortKeyString) {
        if (sortKeyString == null || sortKeyString.isBlank()) {
            return CategorySortKey.defaultKey();
        }
        for (CategorySortKey key : CategorySortKey.values()) {
            if (key.fieldName().equalsIgnoreCase(sortKeyString)
                    || key.name().equalsIgnoreCase(sortKeyString)) {
                return key;
            }
        }
        return CategorySortKey.defaultKey();
    }
}
