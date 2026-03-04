package com.ryuqq.marketplace.application.selleradmin.dto.query;

/**
 * 셀러 관리자 가입 신청 상세 조회 Query.
 *
 * <p>가입 신청 상태 (PENDING_APPROVAL, REJECTED)만 조회합니다.
 *
 * @param sellerAdminId 셀러 관리자 ID (UUIDv7)
 * @author ryu-qqq
 * @since 1.1.0
 */
public record GetSellerAdminApplicationQuery(String sellerAdminId) {

    public static GetSellerAdminApplicationQuery of(String sellerAdminId) {
        return new GetSellerAdminApplicationQuery(sellerAdminId);
    }
}
