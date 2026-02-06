package com.ryuqq.marketplace.application.sellerapplication.dto.query;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.domain.sellerapplication.vo.ApplicationStatus;
import java.util.List;

/**
 * 셀러 입점 신청 검색 파라미터.
 *
 * <p>APP-DTO-003: SearchParams CommonSearchParams 포함 필수
 *
 * @param status 신청 상태 필터 목록 (빈 리스트면 전체)
 * @param searchField 검색 필드 (null이면 전체 필드)
 * @param searchWord 검색어
 * @param searchParams 공통 검색 파라미터 (정렬, 페이징 등)
 */
public record SellerApplicationSearchParams(
        List<ApplicationStatus> status,
        String searchField,
        String searchWord,
        CommonSearchParams searchParams) {

    public SellerApplicationSearchParams {
        status = status != null ? List.copyOf(status) : List.of();
    }

    public static SellerApplicationSearchParams of(
            List<ApplicationStatus> status,
            String searchField,
            String searchWord,
            CommonSearchParams searchParams) {
        return new SellerApplicationSearchParams(status, searchField, searchWord, searchParams);
    }

    public int page() {
        return searchParams.page();
    }

    public int size() {
        return searchParams.size();
    }

    public String sortKey() {
        return searchParams.sortKey();
    }

    public String sortDirection() {
        return searchParams.sortDirection();
    }
}
