package com.ryuqq.fixture.domain;

import com.ryuqq.marketplace.domain.brand.vo.Confidence;

/**
 * Confidence Domain 객체 Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public class ConfidenceFixture {

    private ConfidenceFixture() {
        throw new AssertionError("Utility class");
    }

    /**
     * 기본 Confidence Fixture (확실)
     */
    public static Confidence defaultConfidence() {
        return Confidence.certain();
    }

    /**
     * 불확실 Confidence Fixture
     */
    public static Confidence uncertainConfidence() {
        return Confidence.uncertain();
    }

    /**
     * 없음 Confidence Fixture
     */
    public static Confidence noneConfidence() {
        return Confidence.none();
    }

    /**
     * Custom Confidence Fixture Builder
     */
    public static Confidence customConfidence(double value) {
        return Confidence.of(value);
    }
}
