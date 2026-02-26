package com.ryuqq.marketplace.adapter.out.persistence.legacy.product;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductGroupEntity;

/**
 * LegacyProductGroupEntity 테스트 Fixtures.
 *
 * <p>테스트에서 LegacyProductGroupEntity 관련 객체들을 생성합니다.
 */
public final class LegacyProductGroupEntityFixtures {

    private LegacyProductGroupEntityFixtures() {}

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = null; // 신규 등록 시 ID는 null
    public static final String DEFAULT_PRODUCT_GROUP_NAME = "테스트 상품";
    public static final Long DEFAULT_SELLER_ID = 10L;
    public static final Long DEFAULT_BRAND_ID = 20L;
    public static final Long DEFAULT_CATEGORY_ID = 30L;
    public static final String DEFAULT_OPTION_TYPE = "SINGLE";
    public static final String DEFAULT_MANAGEMENT_TYPE = "MENUAL";
    public static final Long DEFAULT_REGULAR_PRICE = 50000L;
    public static final Long DEFAULT_CURRENT_PRICE = 45000L;
    public static final String DEFAULT_SOLD_OUT_YN = "N";
    public static final String DEFAULT_DISPLAY_YN = "Y";
    public static final String DEFAULT_PRODUCT_CONDITION = "NEW";
    public static final String DEFAULT_ORIGIN = "KR";
    public static final String DEFAULT_STYLE_CODE = "STYLE001";

    // ===== Entity Fixtures =====

    /** 신규 등록용 기본 상품 그룹 Entity 생성 (ID는 null). */
    public static LegacyProductGroupEntity newEntity() {
        return LegacyProductGroupEntity.create(
                DEFAULT_ID,
                DEFAULT_PRODUCT_GROUP_NAME,
                DEFAULT_SELLER_ID,
                DEFAULT_BRAND_ID,
                DEFAULT_CATEGORY_ID,
                DEFAULT_OPTION_TYPE,
                DEFAULT_MANAGEMENT_TYPE,
                DEFAULT_REGULAR_PRICE,
                DEFAULT_CURRENT_PRICE,
                DEFAULT_SOLD_OUT_YN,
                DEFAULT_DISPLAY_YN,
                DEFAULT_PRODUCT_CONDITION,
                DEFAULT_ORIGIN,
                DEFAULT_STYLE_CODE);
    }

    /** 기존 ID를 가진 상품 그룹 Entity 생성. */
    public static LegacyProductGroupEntity entityWithId(Long id) {
        return LegacyProductGroupEntity.create(
                id,
                DEFAULT_PRODUCT_GROUP_NAME,
                DEFAULT_SELLER_ID,
                DEFAULT_BRAND_ID,
                DEFAULT_CATEGORY_ID,
                DEFAULT_OPTION_TYPE,
                DEFAULT_MANAGEMENT_TYPE,
                DEFAULT_REGULAR_PRICE,
                DEFAULT_CURRENT_PRICE,
                DEFAULT_SOLD_OUT_YN,
                DEFAULT_DISPLAY_YN,
                DEFAULT_PRODUCT_CONDITION,
                DEFAULT_ORIGIN,
                DEFAULT_STYLE_CODE);
    }

    /** 품절 상태의 상품 그룹 Entity 생성. */
    public static LegacyProductGroupEntity soldOutEntity() {
        return LegacyProductGroupEntity.create(
                DEFAULT_ID,
                DEFAULT_PRODUCT_GROUP_NAME,
                DEFAULT_SELLER_ID,
                DEFAULT_BRAND_ID,
                DEFAULT_CATEGORY_ID,
                DEFAULT_OPTION_TYPE,
                DEFAULT_MANAGEMENT_TYPE,
                DEFAULT_REGULAR_PRICE,
                DEFAULT_CURRENT_PRICE,
                "Y",
                "N",
                DEFAULT_PRODUCT_CONDITION,
                DEFAULT_ORIGIN,
                DEFAULT_STYLE_CODE);
    }
}
