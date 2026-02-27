package com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping;

/** InboundBrandMapping Admin API 엔드포인트 상수. */
public final class InboundBrandMappingAdminEndpoints {

    private InboundBrandMappingAdminEndpoints() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static final String BASE = "/api/v1/market";

    public static final String BRAND_MAPPINGS = BASE + "/internal/inbound/brand-mappings";

    public static final String BRAND_MAPPING_ID = "/{id}";

    public static final String BATCH = "/batch";

    public static final String PATH_ID = "id";
}
