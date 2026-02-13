package com.ryuqq.marketplace.adapter.in.rest.product;

/**
 * ProductAdminEndpoints - 상품(SKU) Admin API 엔드포인트 상수.
 *
 * <p>API-END-001: Endpoints final class
 *
 * <p>API-END-002: static final 상수
 *
 * <p>API-END-003: Path Variable 상수
 */
public final class ProductAdminEndpoints {

    private ProductAdminEndpoints() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /** 상품(SKU) 기본 경로 */
    public static final String PRODUCTS = "/api/v1/market/products";

    /** Product ID Path Variable */
    public static final String ID = "/{productId}";

    /** Product ID Path Variable 이름 */
    public static final String PATH_PRODUCT_ID = "productId";

    /** 가격 수정 경로 */
    public static final String PRICE = "/price";

    /** 재고 수정 경로 */
    public static final String STOCK = "/stock";

    /** 상태 변경 경로 */
    public static final String STATUS = "/status";
}
