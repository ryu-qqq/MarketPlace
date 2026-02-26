package com.ryuqq.marketplace.adapter.in.rest.shop;

/** Shop Admin API 엔드포인트 상수. */
public final class ShopAdminEndpoints {

    private ShopAdminEndpoints() {}

    private static final String BASE = "/api/v1/market";
    public static final String SHOPS = BASE + "/shops";
    public static final String SHOP_ID = "/{shopId}";
    public static final String PATH_SHOP_ID = "shopId";
}
