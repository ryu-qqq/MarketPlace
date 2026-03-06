package com.ryuqq.marketplace.adapter.in.rest.exchange;

/** Exchange API 엔드포인트 상수. */
public final class ExchangeAdminEndpoints {

    private ExchangeAdminEndpoints() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static final String EXCHANGES = "/api/v1/market/exchanges";
    public static final String SUMMARY = "/summary";
}
