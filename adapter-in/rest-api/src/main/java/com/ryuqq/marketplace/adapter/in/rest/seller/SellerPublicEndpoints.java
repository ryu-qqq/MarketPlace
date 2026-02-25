package com.ryuqq.marketplace.adapter.in.rest.seller;

/**
 * SellerPublicEndpoints - 셀러 공개 API 엔드포인트 상수.
 *
 * <p>인증 없이 접근 가능한 셀러 공개 조회 엔드포인트.
 *
 * <p>API-END-001: Endpoints final class + private 생성자.
 *
 * <p>API-END-002: static final 상수.
 *
 * <p>API-END-003: Path Variable 상수.
 *
 * <p>API-CTR-012: URL 경로 소문자 + 복수형.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class SellerPublicEndpoints {

    private SellerPublicEndpoints() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /** 기본 경로 */
    public static final String BASE = "/api/v1/market";

    /** 셀러 공개 프로필 조회 경로 */
    public static final String SELLER_PROFILE = BASE + "/sellers/{sellerId}/profile";

    /** Seller ID Path Variable 이름 */
    public static final String PATH_SELLER_ID = "sellerId";
}
