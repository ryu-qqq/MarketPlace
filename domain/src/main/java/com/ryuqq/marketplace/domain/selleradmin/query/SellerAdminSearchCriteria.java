package com.ryuqq.marketplace.domain.selleradmin.query;

import com.ryuqq.marketplace.domain.common.vo.DateRange;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.selleradmin.vo.SellerAdminStatus;
import java.util.List;

/**
 * SellerAdmin 검색 조건 Criteria.
 *
 * <p>셀러 관리자 가입 신청 목록 조회 시 사용하는 검색 조건과 페이징 정보를 정의합니다.
 *
 * @param sellerIds 셀러 ID 목록 (null 또는 빈 목록이면 전체 조회)
 * @param status 상태 필터 목록 (빈 리스트면 전체)
 * @param searchField 검색 필드 (null이면 전체 필드 검색)
 * @param searchWord 검색어 (null이면 전체)
 * @param dateRange 검색 날짜 범위 (null이면 제한 없음)
 * @param queryContext 정렬 및 페이징 정보
 * @author ryu-qqq
 * @since 1.1.0
 */
public record SellerAdminSearchCriteria(
        List<Long> sellerIds,
        List<SellerAdminStatus> status,
        SellerAdminSearchField searchField,
        String searchWord,
        DateRange dateRange,
        QueryContext<SellerAdminSortKey> queryContext) {

    public SellerAdminSearchCriteria {
        sellerIds = sellerIds == null ? null : List.copyOf(sellerIds);
        status = status == null ? List.of() : List.copyOf(status);
    }

    /**
     * 검색 조건 생성.
     *
     * @param sellerIds 셀러 ID 목록 (null 또는 빈 목록이면 전체 조회)
     * @param status 상태 필터 목록 (빈 리스트면 전체)
     * @param searchField 검색 필드 (null이면 전체 필드 검색)
     * @param searchWord 검색어 (null이면 전체)
     * @param dateRange 검색 날짜 범위 (null이면 제한 없음)
     * @param queryContext 정렬 및 페이징 정보
     * @return SellerAdminSearchCriteria
     */
    public static SellerAdminSearchCriteria of(
            List<Long> sellerIds,
            List<SellerAdminStatus> status,
            SellerAdminSearchField searchField,
            String searchWord,
            DateRange dateRange,
            QueryContext<SellerAdminSortKey> queryContext) {
        return new SellerAdminSearchCriteria(
                sellerIds, status, searchField, searchWord, dateRange, queryContext);
    }

    /**
     * 기본 검색 조건 생성 (전체 조회, 생성순 내림차순).
     *
     * @return SellerAdminSearchCriteria
     */
    public static SellerAdminSearchCriteria defaultCriteria() {
        return new SellerAdminSearchCriteria(
                null,
                List.of(),
                null,
                null,
                null,
                QueryContext.defaultOf(SellerAdminSortKey.defaultKey()));
    }

    /**
     * 승인 대기 중인 신청만 조회하는 조건 생성.
     *
     * @return SellerAdminSearchCriteria
     */
    public static SellerAdminSearchCriteria pendingOnly() {
        return new SellerAdminSearchCriteria(
                null,
                List.of(SellerAdminStatus.PENDING_APPROVAL),
                null,
                null,
                null,
                QueryContext.defaultOf(SellerAdminSortKey.defaultKey()));
    }

    /** 셀러 ID 필터가 있는지 확인. */
    public boolean hasSellerIds() {
        return sellerIds != null && !sellerIds.isEmpty();
    }

    /** 검색 조건이 있는지 확인. */
    public boolean hasSearchCondition() {
        return searchWord != null && !searchWord.isBlank();
    }

    /** 특정 필드 검색인지 확인. */
    public boolean hasSearchField() {
        return searchField != null;
    }

    /** 상태 필터가 있는지 확인. */
    public boolean hasStatusFilter() {
        return status != null && !status.isEmpty();
    }

    /** 날짜 범위 필터가 있는지 확인. */
    public boolean hasDateRange() {
        return dateRange != null && !dateRange.isEmpty();
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
