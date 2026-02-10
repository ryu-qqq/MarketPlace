package com.ryuqq.marketplace.adapter.in.rest.categorypreset;

/** CategoryPreset Admin API 엔드포인트 상수. */
public final class CategoryPresetAdminEndpoints {

    private CategoryPresetAdminEndpoints() {}

    private static final String BASE = "/api/v1/market";
    public static final String CATEGORY_PRESETS = BASE + "/category-presets";
    public static final String CATEGORY_PRESET_ID = "/{categoryPresetId}";
    public static final String PATH_CATEGORY_PRESET_ID = "categoryPresetId";
}
