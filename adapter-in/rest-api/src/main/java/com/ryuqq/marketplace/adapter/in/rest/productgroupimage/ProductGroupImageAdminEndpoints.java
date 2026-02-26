package com.ryuqq.marketplace.adapter.in.rest.productgroupimage;

/**
 * ProductGroupImage Admin API 엔드포인트 상수.
 *
 * <p>API-END-001: Endpoints final class
 *
 * <p>API-END-002: static final 상수
 *
 * <p>API-END-003: Path Variable 상수
 */
public final class ProductGroupImageAdminEndpoints {

    private ProductGroupImageAdminEndpoints() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /** 상품 그룹 기본 경로 */
    public static final String PRODUCT_GROUPS = "/api/v1/market/product-groups";

    /** ProductGroup ID Path Variable */
    public static final String ID = "/{productGroupId}";

    /** ProductGroup ID Path Variable 이름 */
    public static final String PATH_PRODUCT_GROUP_ID = "productGroupId";

    /** 이미지 경로 */
    public static final String IMAGES = "/images";

    /** 업로드 상태 조회 경로 */
    public static final String UPLOAD_STATUS = "/upload-status";
}
