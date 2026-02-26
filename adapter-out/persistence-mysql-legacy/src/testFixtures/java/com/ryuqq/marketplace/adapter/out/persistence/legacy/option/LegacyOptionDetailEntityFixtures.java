package com.ryuqq.marketplace.adapter.out.persistence.legacy.option;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.entity.LegacyOptionDetailEntity;

/**
 * LegacyOptionDetailEntity 테스트 Fixtures.
 *
 * <p>테스트에서 LegacyOptionDetailEntity 관련 객체들을 생성합니다.
 */
public final class LegacyOptionDetailEntityFixtures {

    private LegacyOptionDetailEntityFixtures() {}

    // ===== 기본 상수 =====
    public static final Long DEFAULT_OPTION_GROUP_ID = 1L;
    public static final String DEFAULT_OPTION_VALUE = "RED";

    // ===== Entity Fixtures =====

    /** 기본 옵션 상세 Entity 생성. */
    public static LegacyOptionDetailEntity defaultEntity() {
        return LegacyOptionDetailEntity.create(DEFAULT_OPTION_GROUP_ID, DEFAULT_OPTION_VALUE);
    }

    /** 옵션 그룹 ID와 값을 지정한 Entity 생성. */
    public static LegacyOptionDetailEntity entityWithGroupIdAndValue(
            long optionGroupId, String optionValue) {
        return LegacyOptionDetailEntity.create(optionGroupId, optionValue);
    }
}
