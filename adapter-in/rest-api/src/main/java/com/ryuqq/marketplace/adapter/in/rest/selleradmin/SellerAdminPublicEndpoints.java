package com.ryuqq.marketplace.adapter.in.rest.selleradmin;

/**
 * SellerAdmin Public API 엔드포인트 상수.
 *
 * <p>인증 없이 접근 가능한 공개 셀러 관리자 엔드포인트.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class SellerAdminPublicEndpoints {

    private SellerAdminPublicEndpoints() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /** 공개 셀러 관리자 API 기본 경로. */
    public static final String BASE = "/api/v1/market/public/seller-admins";

    /** 셀러 관리자 본인 확인 경로. */
    public static final String VERIFY = "/verify";
}
