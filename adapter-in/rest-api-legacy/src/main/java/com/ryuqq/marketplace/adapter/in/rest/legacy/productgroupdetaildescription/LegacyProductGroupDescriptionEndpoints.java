package com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupdetaildescription;

/** 레거시 세토프 호환 상품그룹 상세설명 API 엔드포인트. */
public final class LegacyProductGroupDescriptionEndpoints {

    private LegacyProductGroupDescriptionEndpoints() {}

    private static final String BASE = "/api/v1/legacy";

    public static final String PRODUCT_GROUP_ID = BASE + "/product/group/{productGroupId}";
    public static final String DETAIL_DESCRIPTION = PRODUCT_GROUP_ID + "/detailDescription";
}
