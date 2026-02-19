package com.ryuqq.marketplace.adapter.in.rest.externalbrandmapping;

/** ExternalBrandMapping Admin API 엔드포인트 상수. */
public final class ExternalBrandMappingAdminEndpoints {

    private ExternalBrandMappingAdminEndpoints() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static final String BASE = "/api/v1/market";

    public static final String BRAND_MAPPINGS =
            BASE + "/external-sources/{externalSourceId}/brand-mappings";

    public static final String BRAND_MAPPING_ID = "/{id}";

    public static final String BATCH = "/batch";

    public static final String PATH_EXTERNAL_SOURCE_ID = "externalSourceId";

    public static final String PATH_ID = "id";
}
