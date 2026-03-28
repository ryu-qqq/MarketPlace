package com.ryuqq.marketplace.adapter.in.rest.legacy.product;

/** 레거시 세토프 호환 상품(SKU) API 엔드포인트. */
public final class LegacyProductEndpoints {

    private LegacyProductEndpoints() {}

    private static final String BASE = "/api/v1";

    public static final String PRODUCT_GROUP = BASE + "/product/group";
    public static final String PRODUCT_GROUP_ID = PRODUCT_GROUP + "/{productGroupId}";
    public static final String OPTION = PRODUCT_GROUP_ID + "/option";
    public static final String PRICE = PRODUCT_GROUP_ID + "/price";
    public static final String GROUP_STOCK = PRODUCT_GROUP_ID + "/stock";
}
