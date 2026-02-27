package com.ryuqq.marketplace.adapter.in.rest.cancel;

/** Cancel API 엔드포인트 상수. */
public final class CancelAdminEndpoints {

    private CancelAdminEndpoints() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static final String CANCELS = "/api/v1/market/cancels";
    public static final String SUMMARY = "/summary";
}
