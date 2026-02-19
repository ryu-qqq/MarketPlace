package com.ryuqq.marketplace.domain.externalbrandmapping.query;

import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.externalbrandmapping.vo.ExternalBrandMappingStatus;
import java.util.List;

/**
 * ExternalBrandMapping 검색 조건 Criteria.
 *
 * @param externalSourceId 외부 소스 ID 필터 (null이면 전체)
 * @param statuses 상태 필터 (null/empty면 전체)
 * @param searchField 검색 필드 (null이면 전체 필드 검색)
 * @param searchWord 검색어 (null이면 전체)
 * @param queryContext 정렬 및 페이징 정보
 */
public record ExternalBrandMappingSearchCriteria(
        Long externalSourceId,
        List<ExternalBrandMappingStatus> statuses,
        ExternalBrandMappingSearchField searchField,
        String searchWord,
        QueryContext<ExternalBrandMappingSortKey> queryContext) {

    public ExternalBrandMappingSearchCriteria {
        statuses = statuses == null ? null : List.copyOf(statuses);
    }

    public static ExternalBrandMappingSearchCriteria of(
            Long externalSourceId,
            List<ExternalBrandMappingStatus> statuses,
            ExternalBrandMappingSearchField searchField,
            String searchWord,
            QueryContext<ExternalBrandMappingSortKey> queryContext) {
        return new ExternalBrandMappingSearchCriteria(
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
                : statuses.stream().map(ExternalBrandMappingStatus::name).toList();
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
