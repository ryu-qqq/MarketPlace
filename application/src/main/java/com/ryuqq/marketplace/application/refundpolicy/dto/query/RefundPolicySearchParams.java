package com.ryuqq.marketplace.application.refundpolicy.dto.query;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;

/**
 * 환불정책 검색 파라미터.
 *
 * <p>APP-DTO-003: SearchParams CommonSearchParams 포함 필수
 *
 * <p>APP-DTO-002: Command/Query 인스턴스 메서드 금지
 *
 * @param sellerId 셀러 ID
 * @param searchParams 공통 검색 파라미터 (정렬, 페이징 등)
 * @param active 활성화 여부 (true: 활성만, false: 비활성만, null: 전체)
 */
public record RefundPolicySearchParams(
        Long sellerId, CommonSearchParams searchParams, Boolean active) {

    public static RefundPolicySearchParams of(
            Long sellerId, CommonSearchParams searchParams, Boolean active) {
        return new RefundPolicySearchParams(sellerId, searchParams, active);
    }

    // Delegate methods for convenience
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
