package com.ryuqq.marketplace.adapter.in.rest.externalcategorymapping;

/** ExternalCategoryMapping Admin API 엔드포인트 상수. */
public final class ExternalCategoryMappingAdminEndpoints {

    private ExternalCategoryMappingAdminEndpoints() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static final String BASE = "/api/v1/market";

    public static final String CATEGORY_MAPPINGS =
            BASE + "/external-sources/{externalSourceId}/category-mappings";

    public static final String CATEGORY_MAPPING_ID = "/{id}";

    public static final String BATCH = "/batch";

    public static final String PATH_EXTERNAL_SOURCE_ID = "externalSourceId";

    public static final String PATH_ID = "id";
}
