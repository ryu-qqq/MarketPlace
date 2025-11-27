package com.ryuqq.fixture.domain;

import com.ryuqq.marketplace.domain.brand.vo.DataQualityLevel;

/**
 * DataQualityLevel Domain 객체 Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public class DataQualityLevelFixture {

    private DataQualityLevelFixture() {
        throw new AssertionError("Utility class");
    }

    /**
     * 기본 DataQualityLevel Fixture (높음)
     */
    public static DataQualityLevel defaultDataQualityLevel() {
        return DataQualityLevel.HIGH;
    }

    /**
     * 알 수 없음 DataQualityLevel Fixture
     */
    public static DataQualityLevel unknownDataQualityLevel() {
        return DataQualityLevel.UNKNOWN;
    }

    /**
     * 낮음 DataQualityLevel Fixture
     */
    public static DataQualityLevel lowDataQualityLevel() {
        return DataQualityLevel.LOW;
    }

    /**
     * 중간 DataQualityLevel Fixture
     */
    public static DataQualityLevel midDataQualityLevel() {
        return DataQualityLevel.MID;
    }
}
