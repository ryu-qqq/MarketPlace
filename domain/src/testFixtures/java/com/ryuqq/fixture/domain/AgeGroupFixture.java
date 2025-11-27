package com.ryuqq.fixture.domain;

import com.ryuqq.marketplace.domain.category.vo.AgeGroup;

/**
 * AgeGroup Domain 객체 Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public class AgeGroupFixture {

    private AgeGroupFixture() {
        throw new AssertionError("Utility class");
    }

    /**
     * 기본 AgeGroup Fixture (성인)
     */
    public static AgeGroup defaultAgeGroup() {
        return AgeGroup.ADULT;
    }

    /**
     * 유아 AgeGroup Fixture
     */
    public static AgeGroup infantAgeGroup() {
        return AgeGroup.INFANT;
    }

    /**
     * 어린이 AgeGroup Fixture
     */
    public static AgeGroup kidsAgeGroup() {
        return AgeGroup.KIDS;
    }

    /**
     * 청소년 AgeGroup Fixture
     */
    public static AgeGroup teenAgeGroup() {
        return AgeGroup.TEEN;
    }

    /**
     * 노년 AgeGroup Fixture
     */
    public static AgeGroup seniorAgeGroup() {
        return AgeGroup.SENIOR;
    }

    /**
     * 해당 없음 AgeGroup Fixture
     */
    public static AgeGroup noneAgeGroup() {
        return AgeGroup.NONE;
    }
}
