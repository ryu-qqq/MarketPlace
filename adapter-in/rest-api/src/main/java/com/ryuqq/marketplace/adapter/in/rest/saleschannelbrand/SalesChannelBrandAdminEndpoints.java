package com.ryuqq.marketplace.adapter.in.rest.saleschannelbrand;

/** 외부채널 브랜드 Admin API 엔드포인트 상수. */
public final class SalesChannelBrandAdminEndpoints {

    private SalesChannelBrandAdminEndpoints() {}

    private static final String BASE = "/api/v1/market/sales-channels";
    public static final String BRANDS = BASE + "/{salesChannelId}/brands";
    public static final String BRAND_ID = "/{brandId}";
    public static final String PATH_SALES_CHANNEL_ID = "salesChannelId";
    public static final String PATH_BRAND_ID = "brandId";
}
