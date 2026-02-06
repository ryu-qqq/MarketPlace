package com.ryuqq.marketplace.application.selleradmin.dto.query;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.domain.common.vo.DateRange;
import com.ryuqq.marketplace.domain.selleradmin.vo.SellerAdminStatus;
import java.util.List;

/**
 * 셀러 관리자 가입 신청 검색 파라미터.
 *
 * @param sellerIds 셀러 ID 목록 (null 또는 빈 목록이면 전체 조회)
 * @param status 상태 필터 (PENDING_APPROVAL, REJECTED 등)
 * @param searchField 검색 필드 (loginId, name)
 * @param searchWord 검색어
 * @param dateRange 검색 날짜 범위
 * @param commonSearchParams 공통 검색 파라미터 (page, size, sortKey, sortDirection 등)
 * @author ryu-qqq
 * @since 1.1.0
 */
public record SellerAdminApplicationSearchParams(
        List<Long> sellerIds,
        List<SellerAdminStatus> status,
        String searchField,
        String searchWord,
        DateRange dateRange,
        CommonSearchParams commonSearchParams) {

    public static SellerAdminApplicationSearchParams of(
            List<Long> sellerIds,
            List<SellerAdminStatus> status,
            String searchField,
            String searchWord,
            DateRange dateRange,
            CommonSearchParams commonSearchParams) {
        return new SellerAdminApplicationSearchParams(
                sellerIds, status, searchField, searchWord, dateRange, commonSearchParams);
    }
}
