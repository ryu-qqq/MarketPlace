package com.ryuqq.marketplace.adapter.in.rest.refund;

/** Refund API 엔드포인트 상수. */
public final class RefundAdminEndpoints {

    private RefundAdminEndpoints() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static final String REFUNDS = "/api/v1/market/refunds";
    public static final String SUMMARY = "/summary";
}
