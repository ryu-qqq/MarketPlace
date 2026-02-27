package com.ryuqq.marketplace.adapter.in.rest.settlement;

/** Settlement API 엔드포인트 상수. */
public final class SettlementAdminEndpoints {

    private SettlementAdminEndpoints() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static final String SETTLEMENTS = "/api/v1/market/settlements";
    public static final String DAILY = "/daily";
}
