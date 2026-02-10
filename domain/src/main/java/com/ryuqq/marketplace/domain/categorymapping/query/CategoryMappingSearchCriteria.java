package com.ryuqq.marketplace.domain.categorymapping.query;

import com.ryuqq.marketplace.domain.categorymapping.vo.CategoryMappingStatus;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import java.util.List;
import java.util.Objects;

/** CategoryMapping 검색 조건. */
public record CategoryMappingSearchCriteria(
        List<Long> salesChannelCategoryIds,
        List<Long> internalCategoryIds,
        List<Long> salesChannelIds,
        List<CategoryMappingStatus> statuses,
        CategoryMappingSearchField searchField,
        String searchWord,
        QueryContext<CategoryMappingSortKey> queryContext) {

    public CategoryMappingSearchCriteria {
        salesChannelCategoryIds =
                salesChannelCategoryIds != null ? List.copyOf(salesChannelCategoryIds) : List.of();
        internalCategoryIds =
                internalCategoryIds != null ? List.copyOf(internalCategoryIds) : List.of();
        salesChannelIds = salesChannelIds != null ? List.copyOf(salesChannelIds) : List.of();
        statuses = statuses != null ? List.copyOf(statuses) : List.of();
        Objects.requireNonNull(queryContext, "queryContext must not be null");
    }

    public static CategoryMappingSearchCriteria of(
            List<Long> salesChannelCategoryIds,
            List<Long> internalCategoryIds,
            List<Long> salesChannelIds,
            List<CategoryMappingStatus> statuses,
            CategoryMappingSearchField searchField,
            String searchWord,
            QueryContext<CategoryMappingSortKey> queryContext) {
        return new CategoryMappingSearchCriteria(
                salesChannelCategoryIds,
                internalCategoryIds,
                salesChannelIds,
                statuses,
                searchField,
                searchWord,
                queryContext);
    }

    public static CategoryMappingSearchCriteria defaultCriteria() {
        return new CategoryMappingSearchCriteria(
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                null,
                null,
                QueryContext.defaultOf(CategoryMappingSortKey.defaultKey()));
    }

    public boolean hasSalesChannelCategoryFilter() {
        return !salesChannelCategoryIds.isEmpty();
    }

    public boolean hasInternalCategoryFilter() {
        return !internalCategoryIds.isEmpty();
    }

    public boolean hasSalesChannelFilter() {
        return !salesChannelIds.isEmpty();
    }

    public boolean hasStatusFilter() {
        return !statuses.isEmpty();
    }

    public boolean hasSearchCondition() {
        return searchWord != null && !searchWord.isBlank();
    }

    public boolean hasSearchField() {
        return searchField != null;
    }

    public int size() {
        return queryContext.size();
    }

    public long offset() {
        return queryContext.offset();
    }

    public int page() {
        return queryContext.page();
    }
}
