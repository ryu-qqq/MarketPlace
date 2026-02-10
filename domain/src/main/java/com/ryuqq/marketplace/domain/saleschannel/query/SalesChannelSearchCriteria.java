package com.ryuqq.marketplace.domain.saleschannel.query;

import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.saleschannel.vo.SalesChannelStatus;
import java.util.List;
import java.util.Objects;

/**
 * SalesChannel 검색 조건 Criteria.
 *
 * <p>판매채널 목록 조회 시 사용하는 검색 조건과 페이징 정보를 정의합니다.
 *
 * @param statuses 판매채널 상태 필터 (empty이면 전체)
 * @param searchField 검색 필드 (null이면 전체 필드 대상)
 * @param searchWord 검색어 (null이면 전체)
 * @param queryContext 정렬 및 페이징 정보
 */
public record SalesChannelSearchCriteria(
        List<SalesChannelStatus> statuses,
        SalesChannelSearchField searchField,
        String searchWord,
        QueryContext<SalesChannelSortKey> queryContext) {

    public SalesChannelSearchCriteria {
        statuses = statuses != null ? List.copyOf(statuses) : List.of();
        Objects.requireNonNull(queryContext, "queryContext must not be null");
    }

    public static SalesChannelSearchCriteria of(
            List<SalesChannelStatus> statuses,
            SalesChannelSearchField searchField,
            String searchWord,
            QueryContext<SalesChannelSortKey> queryContext) {
        return new SalesChannelSearchCriteria(statuses, searchField, searchWord, queryContext);
    }

    public static SalesChannelSearchCriteria defaultCriteria() {
        return new SalesChannelSearchCriteria(
                List.of(), null, null, QueryContext.defaultOf(SalesChannelSortKey.defaultKey()));
    }

    /** 상태 필터가 있는지 확인. */
    public boolean hasStatusFilter() {
        return !statuses.isEmpty();
    }

    /** 검색 필드가 지정되었는지 확인. */
    public boolean hasSearchField() {
        return searchField != null;
    }

    /** 검색 조건이 있는지 확인. */
    public boolean hasSearchCondition() {
        return searchWord != null && !searchWord.isBlank();
    }

    /** 페이지 크기 반환 (편의 메서드). */
    public int size() {
        return queryContext.size();
    }

    /** 오프셋 반환 (편의 메서드). */
    public long offset() {
        return queryContext.offset();
    }

    /** 현재 페이지 번호 반환 (편의 메서드). */
    public int page() {
        return queryContext.page();
    }
}
