package com.ryuqq.fixture.domain;

import com.ryuqq.marketplace.domain.brand.vo.DataQuality;
import com.ryuqq.marketplace.domain.brand.vo.DataQualityLevel;

/**
 * DataQuality Domain 객체 Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public class DataQualityFixture {

    private DataQualityFixture() {
        throw new AssertionError("Utility class");
    }

    /**
     * 기본 DataQuality Fixture (높은 품질)
     */
    public static DataQuality defaultDataQuality() {
        return DataQuality.of(DataQualityLevel.HIGH, 80);
    }

    /**
     * 알 수 없음 DataQuality Fixture
     */
    public static DataQuality unknownDataQuality() {
        return DataQuality.unknown();
    }

    /**
     * 점수 기반 DataQuality Fixture
     */
    public static DataQuality scoreBasedDataQuality(int score) {
        return DataQuality.fromScore(score);
    }

    /**
     * Custom DataQuality Fixture Builder
     */
    public static DataQuality customDataQuality(DataQualityLevel level, int score) {
        return DataQuality.of(level, score);
    }
}
