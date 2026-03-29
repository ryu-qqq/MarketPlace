package com.ryuqq.marketplace.adapter.in.rest.order;

/** Order Admin API 엔드포인트 상수. */
public final class OrderAdminEndpoints {

    private OrderAdminEndpoints() {}

    private static final String BASE = "/api/v1/market";
    public static final String ORDERS = BASE + "/orders";
    /** V4 간극: 프론트에겐 orderId, 내부는 orderItemId. */
    public static final String ORDER_ITEM_ID = "/{orderId}";

    public static final String PATH_ORDER_ITEM_ID = "orderId";
    public static final String SUMMARY = "/summary";
    public static final String HISTORIES = "/{orderId}/histories";
}
