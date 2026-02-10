package com.ryuqq.marketplace.domain.shop.query;

import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.shop.vo.ShopStatus;
import java.util.List;
import java.util.Objects;

public record ShopSearchCriteria(
        Long salesChannelId,
        List<ShopStatus> statuses,
        ShopSearchField searchField,
        String searchWord,
        QueryContext<ShopSortKey> queryContext) {

    public ShopSearchCriteria {
        statuses = statuses != null ? List.copyOf(statuses) : List.of();
        Objects.requireNonNull(queryContext, "queryContext must not be null");
    }

    public static ShopSearchCriteria of(
            Long salesChannelId,
            List<ShopStatus> statuses,
            ShopSearchField searchField,
            String searchWord,
            QueryContext<ShopSortKey> queryContext) {
        return new ShopSearchCriteria(
                salesChannelId, statuses, searchField, searchWord, queryContext);
    }

    public static ShopSearchCriteria defaultCriteria() {
        return new ShopSearchCriteria(
                null, List.of(), null, null, QueryContext.defaultOf(ShopSortKey.defaultKey()));
    }

    public static ShopSearchCriteria activeOnly() {
        return new ShopSearchCriteria(
                null,
                List.of(ShopStatus.ACTIVE),
                null,
                null,
                QueryContext.defaultOf(ShopSortKey.defaultKey()));
    }

    public boolean hasSalesChannelFilter() {
        return salesChannelId != null;
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
