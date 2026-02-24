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

    /** 배치 등록 경로 */
    public static final String BATCH = "/batch";

    /** 엑셀 다운로드 경로 */
    public static final String EXCEL = "/excel";
}
