package com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup;

/** 레거시 세토프 호환 상품그룹 API 엔드포인트. */
public final class LegacyProductGroupEndpoints {

    private LegacyProductGroupEndpoints() {}

    private static final String BASE = "/api/v1/legacy";

    public static final String PRODUCT_GROUP = BASE + "/product/group";
    public static final String PRODUCT_GROUP_ID = PRODUCT_GROUP + "/{productGroupId}";
    public static final String GROUP_DISPLAY_YN = PRODUCT_GROUP_ID + "/display-yn";
    public static final String OUT_STOCK = PRODUCT_GROUP_ID + "/out-stock";
}
