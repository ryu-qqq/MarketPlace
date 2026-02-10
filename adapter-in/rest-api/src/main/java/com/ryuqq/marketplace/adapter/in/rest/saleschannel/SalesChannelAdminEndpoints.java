package com.ryuqq.marketplace.adapter.in.rest.saleschannel;

/** 판매채널 Admin API 엔드포인트 상수. */
public final class SalesChannelAdminEndpoints {

    private SalesChannelAdminEndpoints() {}

    private static final String BASE = "/api/v1/market";
    public static final String SALES_CHANNELS = BASE + "/sales-channels";
    public static final String SALES_CHANNEL_ID = "/{salesChannelId}";
    public static final String PATH_SALES_CHANNEL_ID = "salesChannelId";
}
