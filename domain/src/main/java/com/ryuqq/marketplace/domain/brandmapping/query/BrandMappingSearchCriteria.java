package com.ryuqq.marketplace.domain.brandmapping.query;

import com.ryuqq.marketplace.domain.brandmapping.vo.BrandMappingStatus;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import java.util.List;
import java.util.Objects;

/** BrandMapping 검색 조건. */
public record BrandMappingSearchCriteria(
        List<Long> salesChannelBrandIds,
        List<Long> internalBrandIds,
        List<Long> salesChannelIds,
        List<BrandMappingStatus> statuses,
        BrandMappingSearchField searchField,
        String searchWord,
        QueryContext<BrandMappingSortKey> queryContext) {

    public BrandMappingSearchCriteria {
        salesChannelBrandIds =
                salesChannelBrandIds != null ? List.copyOf(salesChannelBrandIds) : List.of();
        internalBrandIds = internalBrandIds != null ? List.copyOf(internalBrandIds) : List.of();
        salesChannelIds = salesChannelIds != null ? List.copyOf(salesChannelIds) : List.of();
        statuses = statuses != null ? List.copyOf(statuses) : List.of();
        Objects.requireNonNull(queryContext, "queryContext must not be null");
    }

    public static BrandMappingSearchCriteria of(
            List<Long> salesChannelBrandIds,
            List<Long> internalBrandIds,
            List<Long> salesChannelIds,
            List<BrandMappingStatus> statuses,
            BrandMappingSearchField searchField,
            String searchWord,
            QueryContext<BrandMappingSortKey> queryContext) {
        return new BrandMappingSearchCriteria(
                salesChannelBrandIds,
                internalBrandIds,
                salesChannelIds,
                statuses,
                searchField,
                searchWord,
                queryContext);
    }

    public static BrandMappingSearchCriteria defaultCriteria() {
        return new BrandMappingSearchCriteria(
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                null,
                null,
                QueryContext.defaultOf(BrandMappingSortKey.defaultKey()));
    }

    public boolean hasSalesChannelBrandFilter() {
        return !salesChannelBrandIds.isEmpty();
    }

    public boolean hasInternalBrandFilter() {
        return !internalBrandIds.isEmpty();
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
