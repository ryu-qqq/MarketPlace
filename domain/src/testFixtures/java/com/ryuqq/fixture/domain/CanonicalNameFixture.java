package com.ryuqq.fixture.domain;

import com.ryuqq.marketplace.domain.brand.vo.CanonicalName;

/**
 * CanonicalName Domain 객체 Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public class CanonicalNameFixture {

    private CanonicalNameFixture() {
        throw new AssertionError("Utility class");
    }

    /**
     * 기본 CanonicalName Fixture
     */
    public static CanonicalName defaultCanonicalName() {
        return CanonicalName.of("Nike");
    }

    /**
     * Custom CanonicalName Fixture Builder
     */
    public static CanonicalName customCanonicalName(String value) {
        return CanonicalName.of(value);
    }
}
