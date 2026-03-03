package com.ryuqq.marketplace.adapter.in.rest.outboundproduct;

/** OMS 엔드포인트 경로 상수. */
public final class OmsEndpoints {

    private OmsEndpoints() {}

    private static final String BASE = "/api/v1/market/oms";

    public static final String PRODUCTS = BASE + "/products";
    public static final String PRODUCT_GROUP_ID = "/{productGroupId}";
    public static final String PATH_PRODUCT_GROUP_ID = "productGroupId";
    public static final String SYNC_HISTORY = PRODUCTS + PRODUCT_GROUP_ID + "/sync-history";
    public static final String SYNC = PRODUCTS + "/sync";
    public static final String SYNC_HISTORY_RETRY = BASE + "/sync-history/{outboxId}/retry";
    public static final String PATH_OUTBOX_ID = "outboxId";
    public static final String PARTNERS = BASE + "/partners";
    public static final String SHOPS = BASE + "/shops";
}
