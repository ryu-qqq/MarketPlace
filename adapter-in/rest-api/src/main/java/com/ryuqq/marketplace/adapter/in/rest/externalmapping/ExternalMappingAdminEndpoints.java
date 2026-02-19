package com.ryuqq.marketplace.adapter.in.rest.externalmapping;

/** ExternalMapping Admin API 엔드포인트 상수. */
public final class ExternalMappingAdminEndpoints {

    private ExternalMappingAdminEndpoints() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static final String BASE = "/api/v1/market";

    public static final String EXTERNAL_MAPPINGS = BASE + "/external-mappings";

    public static final String RESOLVE = "/resolve";
}
