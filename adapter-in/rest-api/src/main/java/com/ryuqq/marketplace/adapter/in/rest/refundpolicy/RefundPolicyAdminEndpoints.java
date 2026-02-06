package com.ryuqq.marketplace.adapter.in.rest.refundpolicy;

/**
 * RefundPolicyAdminEndpoints - 환불정책 Admin API 엔드포인트 상수.
 *
 * <p>API-END-001: Endpoints final class
 *
 * <p>API-END-002: static final 상수
 *
 * <p>API-END-003: Path Variable 상수
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class RefundPolicyAdminEndpoints {

    private RefundPolicyAdminEndpoints() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /** 환불정책 기본 경로 */
    public static final String REFUND_POLICIES =
            "/api/v1/market/sellers/{sellerId}/refund-policies";

    /** Seller ID Path Variable 이름 */
    public static final String PATH_SELLER_ID = "sellerId";

    /** Policy ID Path Variable */
    public static final String ID = "/{policyId}";

    /** Policy ID Path Variable 이름 */
    public static final String PATH_POLICY_ID = "policyId";

    /** 상태 변경 경로 */
    public static final String STATUS = "/status";
}
