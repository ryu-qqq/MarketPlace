package com.ryuqq.marketplace.adapter.in.rest.selleraddress;

/**
 * SellerAddressAdminEndpoints - 셀러 주소 Admin API 엔드포인트 상수.
 *
 * <p>API-END-001: Endpoints final class
 *
 * <p>API-END-002: static final 상수
 */
public final class SellerAddressAdminEndpoints {

    private SellerAddressAdminEndpoints() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /** 셀러 주소 기본 경로 (Command용, sellerId path 포함) */
    public static final String SELLER_ADDRESSES = "/api/v1/market/sellers/{sellerId}/addresses";

    /** 셀러 주소 조회 기본 경로 (Query용, sellerId path 없음) */
    public static final String SELLER_ADDRESSES_QUERY = "/api/v1/market/seller-addresses";

    /** Seller ID Path Variable 이름 */
    public static final String PATH_SELLER_ID = "sellerId";

    /** Address ID Path Variable */
    public static final String ID = "/{addressId}";

    /** Address ID Path Variable 이름 */
    public static final String PATH_ADDRESS_ID = "addressId";

    /** 기본 주소 변경 경로 */
    public static final String DEFAULT = "/default";

    /** 상태 변경 경로 */
    public static final String STATUS = "/status";

    /** 메타데이터 경로 */
    public static final String METADATA = "/metadata";
}
