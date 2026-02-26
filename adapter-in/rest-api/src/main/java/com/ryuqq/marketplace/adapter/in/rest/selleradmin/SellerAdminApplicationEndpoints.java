package com.ryuqq.marketplace.adapter.in.rest.selleradmin;

/**
 * SellerAdminApplicationEndpoints - 셀러 관리자 가입 신청 Admin API 엔드포인트 상수.
 *
 * <p>API-END-001: Endpoints final class
 *
 * <p>API-END-002: static final 상수
 *
 * <p>API-END-003: Path Variable 상수
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class SellerAdminApplicationEndpoints {

    private SellerAdminApplicationEndpoints() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /** 셀러 관리자 가입 신청 기본 경로 */
    public static final String BASE = "/api/v1/market/seller-admin-applications";

    /** Admin ID Path Variable 이름 */
    public static final String PATH_SELLER_ADMIN_ID = "sellerAdminId";

    /** 상세 조회 경로 */
    public static final String DETAIL = "/{sellerAdminId}";

    /** 승인 경로 */
    public static final String APPROVE = "/{sellerAdminId}/approve";

    /** 거절 경로 */
    public static final String REJECT = "/{sellerAdminId}/reject";

    /** 일괄 승인 경로 */
    public static final String BULK_APPROVE = "/bulk-approve";

    /** 일괄 거절 경로 */
    public static final String BULK_REJECT = "/bulk-reject";

    /** 비밀번호 초기화 경로 */
    public static final String RESET_PASSWORD = "/{sellerAdminId}/reset-password";

    /** 비밀번호 변경 경로 */
    public static final String CHANGE_PASSWORD = "/{sellerAdminId}/change-password";
}
