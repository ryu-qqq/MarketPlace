package com.ryuqq.marketplace.domain.shipment.query;

import com.ryuqq.marketplace.domain.common.vo.DateRange;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentStatus;
import java.util.List;

/**
 * Shipment 검색 조건 Criteria.
 *
 * @param statuses 배송 상태 필터 (empty이면 전체)
 * @param searchField 검색 필드 (null이면 전체 필드 검색)
 * @param searchWord 검색어 (null이면 전체)
 * @param dateRange 날짜 범위 (null이면 제한 없음)
 * @param dateField 날짜 검색 대상 필드 (null이면 기본값)
 * @param queryContext 정렬 및 페이징 정보
 */
public record ShipmentSearchCriteria(
        List<ShipmentStatus> statuses,
        ShipmentSearchField searchField,
        String searchWord,
        DateRange dateRange,
        ShipmentDateField dateField,
        QueryContext<ShipmentSortKey> queryContext) {

    public ShipmentSearchCriteria {
        statuses = statuses != null ? List.copyOf(statuses) : List.of();
    }

    public static ShipmentSearchCriteria of(
            List<ShipmentStatus> statuses,
            ShipmentSearchField searchField,
            String searchWord,
            DateRange dateRange,
            ShipmentDateField dateField,
            QueryContext<ShipmentSortKey> queryContext) {
        return new ShipmentSearchCriteria(
                statuses, searchField, searchWord, dateRange, dateField, queryContext);
    }

    public static ShipmentSearchCriteria defaultCriteria() {
        return new ShipmentSearchCriteria(
                List.of(),
                null,
                null,
                null,
                null,
                QueryContext.defaultOf(ShipmentSortKey.defaultKey()));
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
