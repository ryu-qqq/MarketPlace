package com.ryuqq.marketplace.adapter.in.rest.legacy.product;

/** 레거시 세토프 호환 상품 API 엔드포인트. */
public final class LegacyProductEndpoints {

    private LegacyProductEndpoints() {}

    private static final String BASE = "/api/v1/legacy";

    public static final String PRODUCT_GROUP = BASE + "/product/group";
    public static final String PRODUCT_GROUP_ID = PRODUCT_GROUP + "/{productGroupId}";

    public static final String NOTICE = PRODUCT_GROUP_ID + "/notice";
    public static final String IMAGES = PRODUCT_GROUP_ID + "/images";
    public static final String DETAIL_DESCRIPTION = PRODUCT_GROUP_ID + "/detailDescription";
    public static final String PRICE = PRODUCT_GROUP_ID + "/price";
    public static final String GROUP_DISPLAY_YN = PRODUCT_GROUP_ID + "/display-yn";
    public static final String OUT_STOCK = PRODUCT_GROUP_ID + "/out-stock";
    public static final String OPTION = PRODUCT_GROUP_ID + "/option";
    public static final String GROUP_STOCK = PRODUCT_GROUP_ID + "/stock";
}
