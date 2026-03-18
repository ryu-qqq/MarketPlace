package com.ryuqq.marketplace.adapter.in.rest.cancel;

/** Cancel API 엔드포인트 상수. */
public final class CancelAdminEndpoints {

    private CancelAdminEndpoints() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static final String CANCELS = "/api/v1/market/cancels";
    public static final String CANCEL_ID = "/{cancelId}";
    public static final String SUMMARY = "/summary";
    public static final String SELLER_CANCEL_BATCH = "/seller-cancel/batch";
    public static final String APPROVE_BATCH = "/approve/batch";
    public static final String REJECT_BATCH = "/reject/batch";
    public static final String HISTORIES = "/{cancelId}/histories";
}
