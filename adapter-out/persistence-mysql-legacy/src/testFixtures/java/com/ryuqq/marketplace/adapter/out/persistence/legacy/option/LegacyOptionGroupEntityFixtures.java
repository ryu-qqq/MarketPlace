package com.ryuqq.marketplace.adapter.out.persistence.legacy.option;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.entity.LegacyOptionGroupEntity;

/**
 * LegacyOptionGroupEntity 테스트 Fixtures.
 *
 * <p>테스트에서 LegacyOptionGroupEntity 관련 객체들을 생성합니다.
 */
public final class LegacyOptionGroupEntityFixtures {

    private LegacyOptionGroupEntityFixtures() {}

    public static final long DEFAULT_PRODUCT_GROUP_ID = 1L;
    public static final String DEFAULT_OPTION_NAME = "COLOR";
    public static final String OPTION_NAME_SIZE = "SIZE";

    public static LegacyOptionGroupEntity defaultEntity() {
        return LegacyOptionGroupEntity.create(DEFAULT_PRODUCT_GROUP_ID, DEFAULT_OPTION_NAME);
    }

    public static LegacyOptionGroupEntity entityWithName(String optionName) {
        return LegacyOptionGroupEntity.create(DEFAULT_PRODUCT_GROUP_ID, optionName);
    }

    public static LegacyOptionGroupEntity sizeEntity() {
        return LegacyOptionGroupEntity.create(DEFAULT_PRODUCT_GROUP_ID, OPTION_NAME_SIZE);
    }
}
