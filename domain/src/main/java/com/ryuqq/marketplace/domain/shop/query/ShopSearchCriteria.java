package com.ryuqq.marketplace.domain.shop.query;

import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.shop.vo.ShopStatus;
import java.util.List;

/**
 * Shop 검색 조건 Criteria.
 *
 * <p>외부몰 목록 조회 시 사용하는 검색 조건과 페이징 정보를 정의합니다.
 *
 * @param statuses 외부몰 상태 필터 (empty이면 전체)
 * @param searchField 검색 필드 (null이면 전체 필드 검색)
 * @param searchWord 검색어 (null이면 전체)
 * @param queryContext 정렬 및 페이징 정보
 */
public record ShopSearchCriteria(
        List<ShopStatus> statuses,
        ShopSearchField searchField,
        String searchWord,
        QueryContext<ShopSortKey> queryContext) {

    public ShopSearchCriteria {
        statuses = statuses != null ? List.copyOf(statuses) : List.of();
    }

    public static ShopSearchCriteria of(
            List<ShopStatus> statuses,
            ShopSearchField searchField,
            String searchWord,
            QueryContext<ShopSortKey> queryContext) {
        return new ShopSearchCriteria(statuses, searchField, searchWord, queryContext);
    }

    public static ShopSearchCriteria defaultCriteria() {
        return new ShopSearchCriteria(
                List.of(), null, null, QueryContext.defaultOf(ShopSortKey.defaultKey()));
    }

    public static ShopSearchCriteria activeOnly() {
        return new ShopSearchCriteria(
                List.of(ShopStatus.ACTIVE),
                null,
                null,
                QueryContext.defaultOf(ShopSortKey.defaultKey()));
    }

    /** 상태 필터가 있는지 확인. */
    public boolean hasStatusFilter() {
        return !statuses.isEmpty();
    }

    /** 검색 조건이 있는지 확인. */
    public boolean hasSearchCondition() {
        return searchWord != null && !searchWord.isBlank();
    }

    /** 특정 필드 검색인지 확인. */
    public boolean hasSearchField() {
        return searchField != null;
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
