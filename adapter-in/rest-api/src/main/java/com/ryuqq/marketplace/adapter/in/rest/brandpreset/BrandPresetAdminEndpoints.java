package com.ryuqq.marketplace.adapter.in.rest.brandpreset;

/** BrandPreset Admin API 엔드포인트 상수. */
public final class BrandPresetAdminEndpoints {

    private BrandPresetAdminEndpoints() {}

    private static final String BASE = "/api/v1/market";
    public static final String BRAND_PRESETS = BASE + "/brand-presets";
    public static final String BRAND_PRESET_ID = "/{brandPresetId}";
    public static final String PATH_BRAND_PRESET_ID = "brandPresetId";
}
