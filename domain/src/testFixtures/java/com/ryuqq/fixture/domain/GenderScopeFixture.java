package com.ryuqq.fixture.domain;

import com.ryuqq.marketplace.domain.category.vo.GenderScope;

/**
 * GenderScope Domain 객체 Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public class GenderScopeFixture {

    private GenderScopeFixture() {
        throw new AssertionError("Utility class");
    }

    /**
     * 기본 GenderScope Fixture (남녀공용)
     */
    public static GenderScope defaultGenderScope() {
        return GenderScope.UNISEX;
    }

    /**
     * 남성 GenderScope Fixture
     */
    public static GenderScope menGenderScope() {
        return GenderScope.MEN;
    }

    /**
     * 여성 GenderScope Fixture
     */
    public static GenderScope womenGenderScope() {
        return GenderScope.WOMEN;
    }

    /**
     * 아동 GenderScope Fixture
     */
    public static GenderScope kidsGenderScope() {
        return GenderScope.KIDS;
    }

    /**
     * 해당 없음 GenderScope Fixture
     */
    public static GenderScope noneGenderScope() {
        return GenderScope.NONE;
    }
}
