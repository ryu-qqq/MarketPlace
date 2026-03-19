package com.ryuqq.marketplace.adapter.in.rest.settlement;

/** Settlement API 엔드포인트 상수. */
public final class SettlementAdminEndpoints {

    private SettlementAdminEndpoints() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static final String SETTLEMENTS = "/api/v1/market/settlements";
    public static final String DAILY = "/daily";
    public static final String SETTLEMENT_ID = "/{settlementId}";
    public static final String ENTRIES = "/entries";
    public static final String SETTLEMENT_ENTRIES = "/{settlementId}/entries";
    public static final String HOLD = "/{settlementId}/hold";
    public static final String RELEASE = "/{settlementId}/release";
    public static final String ENTRY_ID = "/entries/{entryId}";
    public static final String COMPLETE_BATCH = "/complete/batch";
    public static final String HOLD_BATCH = "/hold/batch";
    public static final String RELEASE_BATCH = "/release/batch";
    public static final String STATUS_BATCH = "/status/batch";
}
