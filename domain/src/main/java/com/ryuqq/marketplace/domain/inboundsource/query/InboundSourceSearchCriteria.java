package com.ryuqq.marketplace.domain.inboundsource.query;

import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.inboundsource.vo.InboundSourceStatus;
import com.ryuqq.marketplace.domain.inboundsource.vo.InboundSourceType;
import java.util.List;

/**
 * InboundSource 검색 조건 Criteria.
 *
 * @param types 유형 필터 (null/empty면 전체)
 * @param statuses 상태 필터 (null/empty면 전체)
 * @param searchField 검색 필드 (null이면 전체 필드 검색)
 * @param searchWord 검색어 (null이면 전체)
 * @param queryContext 정렬 및 페이징 정보
 */
public record InboundSourceSearchCriteria(
        List<InboundSourceType> types,
        List<InboundSourceStatus> statuses,
        InboundSourceSearchField searchField,
        String searchWord,
        QueryContext<InboundSourceSortKey> queryContext) {

    public InboundSourceSearchCriteria {
        types = types == null ? null : List.copyOf(types);
        statuses = statuses == null ? null : List.copyOf(statuses);
    }

    public static InboundSourceSearchCriteria of(
            List<InboundSourceType> types,
            List<InboundSourceStatus> statuses,
            InboundSourceSearchField searchField,
            String searchWord,
            QueryContext<InboundSourceSortKey> queryContext) {
        return new InboundSourceSearchCriteria(
                types, statuses, searchField, searchWord, queryContext);
    }

    public boolean hasTypesFilter() {
        return types != null && !types.isEmpty();
    }

    public List<String> typeNames() {
        return types == null ? List.of() : types.stream().map(InboundSourceType::name).toList();
    }

    public boolean hasStatusesFilter() {
        return statuses != null && !statuses.isEmpty();
    }

    public List<String> statusNames() {
        return statuses == null
                ? List.of()
                : statuses.stream().map(InboundSourceStatus::name).toList();
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
