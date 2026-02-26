package com.ryuqq.marketplace.domain.canonicaloption.query;

import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import java.util.Objects;

/** 캐노니컬 옵션 그룹 검색 조건. */
public record CanonicalOptionGroupSearchCriteria(
        Boolean active,
        String searchField,
        String searchWord,
        QueryContext<CanonicalOptionGroupSortKey> queryContext) {

    public CanonicalOptionGroupSearchCriteria {
        Objects.requireNonNull(queryContext, "queryContext must not be null");
    }

    public boolean hasActiveFilter() {
        return active != null;
    }

    public boolean hasSearchFilter() {
        return searchField != null
                && !searchField.isBlank()
                && searchWord != null
                && !searchWord.isBlank();
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
