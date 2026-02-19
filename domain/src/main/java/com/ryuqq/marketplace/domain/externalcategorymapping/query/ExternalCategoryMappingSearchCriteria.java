package com.ryuqq.marketplace.domain.externalcategorymapping.query;

import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.externalcategorymapping.vo.ExternalCategoryMappingStatus;
import java.util.List;

/**
 * ExternalCategoryMapping 검색 조건 Criteria.
 *
 * @param externalSourceId 외부 소스 ID 필터 (null이면 전체)
 * @param statuses 상태 필터 (null/empty면 전체)
 * @param searchField 검색 필드 (null이면 전체 필드 검색)
 * @param searchWord 검색어 (null이면 전체)
 * @param queryContext 정렬 및 페이징 정보
 */
public record ExternalCategoryMappingSearchCriteria(
        Long externalSourceId,
        List<ExternalCategoryMappingStatus> statuses,
        ExternalCategoryMappingSearchField searchField,
        String searchWord,
        QueryContext<ExternalCategoryMappingSortKey> queryContext) {

    public ExternalCategoryMappingSearchCriteria {
        statuses = statuses == null ? null : List.copyOf(statuses);
    }

    public static ExternalCategoryMappingSearchCriteria of(
            Long externalSourceId,
            List<ExternalCategoryMappingStatus> statuses,
            ExternalCategoryMappingSearchField searchField,
            String searchWord,
            QueryContext<ExternalCategoryMappingSortKey> queryContext) {
        return new ExternalCategoryMappingSearchCriteria(
                externalSourceId, statuses, searchField, searchWord, queryContext);
    }

    public boolean hasExternalSourceIdFilter() {
        return externalSourceId != null;
    }

    public boolean hasStatusesFilter() {
        return statuses != null && !statuses.isEmpty();
    }

    public List<String> statusNames() {
        return statuses == null
                ? List.of()
                : statuses.stream().map(ExternalCategoryMappingStatus::name).toList();
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
