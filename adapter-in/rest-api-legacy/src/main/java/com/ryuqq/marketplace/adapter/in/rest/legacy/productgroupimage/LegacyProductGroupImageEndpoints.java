package com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupimage;

/** 레거시 세토프 호환 상품그룹 이미지 API 엔드포인트. */
public final class LegacyProductGroupImageEndpoints {

    private LegacyProductGroupImageEndpoints() {}

    private static final String BASE = "/api/v1";

    public static final String PRODUCT_GROUP_ID = BASE + "/product/group/{productGroupId}";
    public static final String IMAGES = PRODUCT_GROUP_ID + "/images";
}
