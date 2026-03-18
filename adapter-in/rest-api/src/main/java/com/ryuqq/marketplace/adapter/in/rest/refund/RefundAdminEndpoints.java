package com.ryuqq.marketplace.adapter.in.rest.refund;

/** Refund API 엔드포인트 상수. */
public final class RefundAdminEndpoints {

    private RefundAdminEndpoints() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static final String REFUNDS = "/api/v1/market/refunds";
    public static final String REFUND_CLAIM_ID = "/{refundClaimId}";
    public static final String SUMMARY = "/summary";
    public static final String REQUEST_BATCH = "/request/batch";
    public static final String APPROVE_BATCH = "/approve/batch";
    public static final String REJECT_BATCH = "/reject/batch";
    public static final String HISTORIES = "/{refundClaimId}/histories";
}
