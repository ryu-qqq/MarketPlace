package com.ryuqq.marketplace.adapter.in.rest.externalsource;

/** ExternalSource Admin API 엔드포인트 상수. */
public final class ExternalSourceAdminEndpoints {

    private ExternalSourceAdminEndpoints() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static final String BASE = "/api/v1/market";

    public static final String EXTERNAL_SOURCES = BASE + "/external-sources";

    public static final String EXTERNAL_SOURCE_ID = "/{externalSourceId}";

    public static final String PATH_EXTERNAL_SOURCE_ID = "externalSourceId";

    public static final String BRAND_MAPPINGS = "/brand-mappings";

    public static final String CATEGORY_MAPPINGS = "/category-mappings";

    public static final String BATCH = "/batch";
}
