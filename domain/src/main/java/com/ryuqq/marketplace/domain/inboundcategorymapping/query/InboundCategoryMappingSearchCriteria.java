package com.ryuqq.marketplace.domain.inboundcategorymapping.query;

import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.inboundcategorymapping.vo.InboundCategoryMappingStatus;
import java.util.List;

/**
 * InboundCategoryMapping 검색 조건 Criteria.
 *
 * @param inboundSourceId 외부 소스 ID 필터 (null이면 전체)
 * @param statuses 상태 필터 (null/empty면 전체)
 * @param searchField 검색 필드 (null이면 전체 필드 검색)
 * @param searchWord 검색어 (null이면 전체)
 * @param queryContext 정렬 및 페이징 정보
 */
public record InboundCategoryMappingSearchCriteria(
        Long inboundSourceId,
        List<InboundCategoryMappingStatus> statuses,
        InboundCategoryMappingSearchField searchField,
        String searchWord,
        QueryContext<InboundCategoryMappingSortKey> queryContext) {

    public InboundCategoryMappingSearchCriteria {
        statuses = statuses == null ? null : List.copyOf(statuses);
    }

    public static InboundCategoryMappingSearchCriteria of(
            Long inboundSourceId,
            List<InboundCategoryMappingStatus> statuses,
            InboundCategoryMappingSearchField searchField,
            String searchWord,
            QueryContext<InboundCategoryMappingSortKey> queryContext) {
        return new InboundCategoryMappingSearchCriteria(
                inboundSourceId, statuses, searchField, searchWord, queryContext);
    }

    public boolean hasInboundSourceIdFilter() {
        return inboundSourceId != null;
    }

    public boolean hasStatusesFilter() {
        return statuses != null && !statuses.isEmpty();
    }

    public List<String> statusNames() {
        return statuses == null
                ? List.of()
                : statuses.stream().map(InboundCategoryMappingStatus::name).toList();
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
