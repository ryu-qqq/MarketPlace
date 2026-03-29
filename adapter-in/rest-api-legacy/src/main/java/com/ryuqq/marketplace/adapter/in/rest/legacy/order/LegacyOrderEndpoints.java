package com.ryuqq.marketplace.adapter.in.rest.legacy.order;

/** 레거시 세토프 호환 주문 API 엔드포인트. */
public final class LegacyOrderEndpoints {

    private LegacyOrderEndpoints() {}

    private static final String BASE = "/api/v1";

    public static final String ORDER = BASE + "/order";
    public static final String ORDER_ID = ORDER + "/{orderId}";
    public static final String ORDER_HISTORY = ORDER + "/history/{orderId}";
    public static final String ORDERS = BASE + "/orders";
}
