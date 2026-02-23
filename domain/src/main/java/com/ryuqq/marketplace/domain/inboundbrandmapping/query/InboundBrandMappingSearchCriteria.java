package com.ryuqq.marketplace.domain.inboundbrandmapping.query;

import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.inboundbrandmapping.vo.InboundBrandMappingStatus;
import java.util.List;

/**
 * InboundBrandMapping 검색 조건 Criteria.
 *
 * @param inboundSourceId 외부 소스 ID 필터 (null이면 전체)
 * @param statuses 상태 필터 (null/empty면 전체)
 * @param searchField 검색 필드 (null이면 전체 필드 검색)
 * @param searchWord 검색어 (null이면 전체)
 * @param queryContext 정렬 및 페이징 정보
 */
public record InboundBrandMappingSearchCriteria(
        Long inboundSourceId,
        List<InboundBrandMappingStatus> statuses,
        InboundBrandMappingSearchField searchField,
        String searchWord,
        QueryContext<InboundBrandMappingSortKey> queryContext) {

    public InboundBrandMappingSearchCriteria {
        statuses = statuses == null ? null : List.copyOf(statuses);
    }

    public static InboundBrandMappingSearchCriteria of(
            Long inboundSourceId,
            List<InboundBrandMappingStatus> statuses,
            InboundBrandMappingSearchField searchField,
            String searchWord,
            QueryContext<InboundBrandMappingSortKey> queryContext) {
        return new InboundBrandMappingSearchCriteria(
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
                : statuses.stream().map(InboundBrandMappingStatus::name).toList();
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
