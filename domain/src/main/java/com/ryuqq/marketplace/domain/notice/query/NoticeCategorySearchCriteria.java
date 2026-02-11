package com.ryuqq.marketplace.domain.notice.query;

import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import java.util.Objects;

/** 고시정보 카테고리 검색 조건. */
public record NoticeCategorySearchCriteria(
        Boolean active,
        String searchField,
        String searchWord,
        QueryContext<NoticeCategorySortKey> queryContext) {

    public NoticeCategorySearchCriteria {
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
