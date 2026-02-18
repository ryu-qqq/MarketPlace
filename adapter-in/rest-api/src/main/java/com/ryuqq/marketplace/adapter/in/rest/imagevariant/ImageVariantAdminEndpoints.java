package com.ryuqq.marketplace.adapter.in.rest.imagevariant;

/**
 * ImageVariant Admin API 엔드포인트 상수.
 *
 * <p>API-END-001: Endpoints final class
 *
 * <p>API-END-002: static final 상수
 *
 * <p>API-END-003: Path Variable 상수
 */
public final class ImageVariantAdminEndpoints {

    private ImageVariantAdminEndpoints() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /** 이미지 Variant 기본 경로 */
    public static final String IMAGE_VARIANTS = "/api/v1/market/image-variants";

    /** ProductGroup ID Path Variable 이름 */
    public static final String PATH_PRODUCT_GROUP_ID = "productGroupId";

    /** ProductGroup 경로 */
    public static final String PRODUCT_GROUP = "/product-groups/{productGroupId}";

    /** Image ID Path Variable */
    public static final String IMAGE_ID = "/{imageId}";

    /** Image ID Path Variable 이름 */
    public static final String PATH_IMAGE_ID = "imageId";

    /** Variant 조회 경로 */
    public static final String VARIANTS = PRODUCT_GROUP + "/images" + IMAGE_ID;

    /** 변환 요청 경로 */
    public static final String TRANSFORM_REQUEST = PRODUCT_GROUP + "/transform";
}
