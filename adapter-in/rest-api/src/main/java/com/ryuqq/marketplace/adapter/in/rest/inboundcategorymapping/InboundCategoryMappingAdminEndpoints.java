package com.ryuqq.marketplace.adapter.in.rest.inboundcategorymapping;

/** InboundCategoryMapping Admin API 엔드포인트 상수. */
public final class InboundCategoryMappingAdminEndpoints {

    private InboundCategoryMappingAdminEndpoints() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static final String BASE = "/api/v1/market";

    public static final String CATEGORY_MAPPINGS = BASE + "/internal/inbound/category-mappings";

    public static final String CATEGORY_MAPPING_ID = "/{id}";

    public static final String BATCH = "/batch";

    public static final String PATH_ID = "id";
}
