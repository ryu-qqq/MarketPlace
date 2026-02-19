package com.ryuqq.marketplace.domain.externalsource.query;

import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.externalsource.vo.ExternalSourceStatus;
import com.ryuqq.marketplace.domain.externalsource.vo.ExternalSourceType;
import java.util.List;

/**
 * ExternalSource 검색 조건 Criteria.
 *
 * @param types 유형 필터 (null/empty면 전체)
 * @param statuses 상태 필터 (null/empty면 전체)
 * @param searchField 검색 필드 (null이면 전체 필드 검색)
 * @param searchWord 검색어 (null이면 전체)
 * @param queryContext 정렬 및 페이징 정보
 */
public record ExternalSourceSearchCriteria(
        List<ExternalSourceType> types,
        List<ExternalSourceStatus> statuses,
        ExternalSourceSearchField searchField,
        String searchWord,
        QueryContext<ExternalSourceSortKey> queryContext) {

    public ExternalSourceSearchCriteria {
        types = types == null ? null : List.copyOf(types);
        statuses = statuses == null ? null : List.copyOf(statuses);
    }

    public static ExternalSourceSearchCriteria of(
            List<ExternalSourceType> types,
            List<ExternalSourceStatus> statuses,
            ExternalSourceSearchField searchField,
            String searchWord,
            QueryContext<ExternalSourceSortKey> queryContext) {
        return new ExternalSourceSearchCriteria(
                types, statuses, searchField, searchWord, queryContext);
    }

    public boolean hasTypesFilter() {
        return types != null && !types.isEmpty();
    }

    public List<String> typeNames() {
        return types == null ? List.of() : types.stream().map(ExternalSourceType::name).toList();
    }

    public boolean hasStatusesFilter() {
        return statuses != null && !statuses.isEmpty();
    }

    public List<String> statusNames() {
        return statuses == null
                ? List.of()
                : statuses.stream().map(ExternalSourceStatus::name).toList();
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
