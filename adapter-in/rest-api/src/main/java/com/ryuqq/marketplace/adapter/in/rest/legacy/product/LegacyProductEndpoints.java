package com.ryuqq.marketplace.adapter.in.rest.legacy.product;

/** 레거시 세토프 호환 상품 API 엔드포인트. */
public final class LegacyProductEndpoints {

    private LegacyProductEndpoints() {}

    private static final String BASE = "/api/v1/legacy";

    public static final String PRODUCT_GROUP = BASE + "/product/group";
    public static final String PRODUCT_GROUP_ID = PRODUCT_GROUP + "/{productGroupId}";
    public static final String PRODUCT_GROUP_UUID = PRODUCT_GROUP + "/uuid/{externalProductUuId}";
    public static final String PRODUCT_GROUPS = BASE + "/products/group";

    public static final String NOTICE = PRODUCT_GROUP_ID + "/notice";
    public static final String DELIVERY_NOTICE = NOTICE + "/delivery";
    public static final String REFUND_NOTICE = NOTICE + "/refund";
    public static final String IMAGES = PRODUCT_GROUP_ID + "/images";
    public static final String DETAIL_DESCRIPTION = PRODUCT_GROUP_ID + "/detailDescription";
    public static final String CATEGORY = PRODUCT_GROUP_ID + "/category";
    public static final String PRICE = PRODUCT_GROUP_ID + "/price";
    public static final String PRICE_BULK = PRODUCT_GROUP + "/price/bulk";
    public static final String GROUP_DISPLAY_YN = PRODUCT_GROUP_ID + "/display-yn";
    public static final String PRODUCT_DISPLAY_YN = BASE + "/product/{productId}/display-yn";
    public static final String OUT_STOCK = PRODUCT_GROUP_ID + "/out-stock";
    public static final String OPTION = PRODUCT_GROUP_ID + "/option";
    public static final String DELETE_GROUPS = BASE + "/product/groups";
    public static final String PRODUCT_STOCK = BASE + "/product/{productId}/stock";
    public static final String GROUP_STOCK = PRODUCT_GROUP_ID + "/stock";
}
