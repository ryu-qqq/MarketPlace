package com.ryuqq.marketplace.domain.brand.query;

import com.ryuqq.marketplace.domain.brand.vo.BrandStatus;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import java.util.List;

/**
 * Brand 검색 조건 Criteria.
 *
 * <p>브랜드 목록 조회 시 사용하는 검색 조건과 페이징 정보를 정의합니다.
 *
 * @param statuses 브랜드 상태 필터 (empty이면 전체)
 * @param searchField 검색 필드 (null이면 전체 필드 검색)
 * @param searchWord 검색어 (null이면 전체)
 * @param queryContext 정렬 및 페이징 정보
 */
public record BrandSearchCriteria(
        List<BrandStatus> statuses,
        BrandSearchField searchField,
        String searchWord,
        QueryContext<BrandSortKey> queryContext) {

    public BrandSearchCriteria {
        statuses = statuses != null ? List.copyOf(statuses) : List.of();
    }

    public static BrandSearchCriteria of(
            List<BrandStatus> statuses,
            BrandSearchField searchField,
            String searchWord,
            QueryContext<BrandSortKey> queryContext) {
        return new BrandSearchCriteria(statuses, searchField, searchWord, queryContext);
    }

    public static BrandSearchCriteria defaultCriteria() {
        return new BrandSearchCriteria(
                List.of(), null, null, QueryContext.defaultOf(BrandSortKey.defaultKey()));
    }

    public static BrandSearchCriteria activeOnly() {
        return new BrandSearchCriteria(
                List.of(BrandStatus.ACTIVE),
                null,
                null,
                QueryContext.defaultOf(BrandSortKey.defaultKey()));
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
