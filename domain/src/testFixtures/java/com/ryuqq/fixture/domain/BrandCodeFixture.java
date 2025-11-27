package com.ryuqq.fixture.domain;

import com.ryuqq.marketplace.domain.brand.vo.BrandCode;

/**
 * BrandCode Domain 객체 Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public class BrandCodeFixture {

    private BrandCodeFixture() {
        throw new AssertionError("Utility class");
    }

    /**
     * 기본 BrandCode Fixture
     */
    public static BrandCode defaultBrandCode() {
        return BrandCode.of("NIKE");
    }

    /**
     * Custom BrandCode Fixture Builder
     */
    public static BrandCode customBrandCode(String value) {
        return BrandCode.of(value);
    }
}
