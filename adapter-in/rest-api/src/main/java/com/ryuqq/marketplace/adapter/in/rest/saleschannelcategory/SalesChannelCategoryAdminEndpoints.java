package com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory;

/** 외부채널 카테고리 Admin API 엔드포인트 상수. */
public final class SalesChannelCategoryAdminEndpoints {

    private SalesChannelCategoryAdminEndpoints() {}

    private static final String BASE = "/api/v1/market/sales-channels";
    public static final String CATEGORIES = BASE + "/{salesChannelId}/categories";
    public static final String PATH_SALES_CHANNEL_ID = "salesChannelId";
}
