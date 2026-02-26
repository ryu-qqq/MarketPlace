package com.ryuqq.marketplace.adapter.out.persistence.legacy.option;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.entity.LegacyOptionGroupEntity;

/**
 * LegacyOptionGroupEntity 테스트 Fixtures.
 *
 * <p>테스트에서 LegacyOptionGroupEntity 관련 객체들을 생성합니다.
 */
public final class LegacyOptionGroupEntityFixtures {

    private LegacyOptionGroupEntityFixtures() {}

    // ===== 기본 상수 =====
    public static final String DEFAULT_OPTION_NAME = "COLOR";
    public static final String OPTION_NAME_SIZE = "SIZE";

    // ===== Entity Fixtures =====

    /** 기본 옵션 그룹 Entity 생성. */
    public static LegacyOptionGroupEntity defaultEntity() {
        return LegacyOptionGroupEntity.create(DEFAULT_OPTION_NAME);
    }

    /** 옵션 이름을 지정한 Entity 생성. */
    public static LegacyOptionGroupEntity entityWithName(String optionName) {
        return LegacyOptionGroupEntity.create(optionName);
    }

    /** 사이즈 옵션 그룹 Entity 생성. */
    public static LegacyOptionGroupEntity sizeEntity() {
        return LegacyOptionGroupEntity.create(OPTION_NAME_SIZE);
    }
}
