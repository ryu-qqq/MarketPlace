package com.ryuqq.marketplace.domain.sellerapplication.query;

import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.sellerapplication.vo.ApplicationStatus;
import java.util.List;

/**
 * SellerApplication 검색 조건 Criteria.
 *
 * <p>입점 신청 목록 조회 시 사용하는 검색 조건과 페이징 정보를 정의합니다.
 *
 * @param status 신청 상태 필터 목록 (빈 리스트면 전체)
 * @param searchField 검색 필드 (null이면 전체 필드 검색)
 * @param searchWord 검색어 (null이면 전체)
 * @param queryContext 정렬 및 페이징 정보
 */
public record SellerApplicationSearchCriteria(
        List<ApplicationStatus> status,
        SellerApplicationSearchField searchField,
        String searchWord,
        QueryContext<SellerApplicationSortKey> queryContext) {

    public SellerApplicationSearchCriteria {
        status = status == null ? List.of() : List.copyOf(status);
    }

    /**
     * 검색 조건 생성.
     *
     * @param status 신청 상태 필터 목록 (빈 리스트면 전체)
     * @param searchField 검색 필드 (null이면 전체 필드 검색)
     * @param searchWord 검색어 (null이면 전체)
     * @param queryContext 정렬 및 페이징 정보
     * @return SellerApplicationSearchCriteria
     */
    public static SellerApplicationSearchCriteria of(
            List<ApplicationStatus> status,
            SellerApplicationSearchField searchField,
            String searchWord,
            QueryContext<SellerApplicationSortKey> queryContext) {
        return new SellerApplicationSearchCriteria(status, searchField, searchWord, queryContext);
    }

    /**
     * 기본 검색 조건 생성 (전체 조회, 신청순 내림차순).
     *
     * @return SellerApplicationSearchCriteria
     */
    public static SellerApplicationSearchCriteria defaultCriteria() {
        return new SellerApplicationSearchCriteria(
                List.of(),
                null,
                null,
                QueryContext.defaultOf(SellerApplicationSortKey.defaultKey()));
    }

    /**
     * 대기 중인 신청만 조회하는 조건 생성.
     *
     * @return SellerApplicationSearchCriteria
     */
    public static SellerApplicationSearchCriteria pendingOnly() {
        return new SellerApplicationSearchCriteria(
                List.of(ApplicationStatus.PENDING),
                null,
                null,
                QueryContext.defaultOf(SellerApplicationSortKey.defaultKey()));
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
