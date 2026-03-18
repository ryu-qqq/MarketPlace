package com.ryuqq.marketplace.adapter.in.rest.exchange;

/** Exchange API 엔드포인트 상수. */
public final class ExchangeAdminEndpoints {

    private ExchangeAdminEndpoints() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static final String EXCHANGES = "/api/v1/market/exchanges";
    public static final String EXCHANGE_CLAIM_ID = "/{exchangeClaimId}";
    public static final String SUMMARY = "/summary";
    public static final String REQUEST_BATCH = "/request/batch";
    public static final String APPROVE_BATCH = "/approve/batch";
    public static final String COLLECT_BATCH = "/collect/batch";
    public static final String PREPARE_BATCH = "/prepare/batch";
    public static final String REJECT_BATCH = "/reject/batch";
    public static final String SHIP_BATCH = "/ship/batch";
    public static final String COMPLETE_BATCH = "/complete/batch";
    public static final String CONVERT_TO_REFUND_BATCH = "/convert-to-refund/batch";
    public static final String HISTORIES = "/{exchangeClaimId}/histories";
}
