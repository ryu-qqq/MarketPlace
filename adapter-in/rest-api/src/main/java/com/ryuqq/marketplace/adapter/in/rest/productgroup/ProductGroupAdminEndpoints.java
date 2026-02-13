package com.ryuqq.marketplace.adapter.in.rest.productgroup;

/**
 * ProductGroupAdminEndpoints - 상품 그룹 Admin API 엔드포인트 상수.
 *
 * <p>API-END-001: Endpoints final class
 *
 * <p>API-END-002: static final 상수
 *
 * <p>API-END-003: Path Variable 상수
 */
public final class ProductGroupAdminEndpoints {

    private ProductGroupAdminEndpoints() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /** 상품 그룹 기본 경로 */
    public static final String PRODUCT_GROUPS = "/api/v1/market/product-groups";

    /** ProductGroup ID Path Variable */
    public static final String ID = "/{productGroupId}";

    /** ProductGroup ID Path Variable 이름 */
    public static final String PATH_PRODUCT_GROUP_ID = "productGroupId";

    /** 상태 변경 경로 */
    public static final String STATUS = "/status";

    /** 기본 정보 수정 경로 */
    public static final String BASIC_INFO = "/basic-info";

    /** 이미지 수정 경로 */
    public static final String IMAGES = "/images";

    /** 상세 설명 수정 경로 */
    public static final String DESCRIPTION = "/description";

    /** 고시정보 수정 경로 */
    public static final String NOTICE = "/notice";
}
