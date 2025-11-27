package com.ryuqq.fixture.domain;

import com.ryuqq.marketplace.domain.brand.vo.Country;

/**
 * Country Domain 객체 Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public class CountryFixture {

    private CountryFixture() {
        throw new AssertionError("Utility class");
    }

    /**
     * 기본 Country Fixture (한국)
     */
    public static Country defaultCountry() {
        return Country.of("KR");
    }

    /**
     * 미국 Country Fixture
     */
    public static Country usCountry() {
        return Country.of("US");
    }

    /**
     * 프랑스 Country Fixture
     */
    public static Country franceCountry() {
        return Country.of("FR");
    }

    /**
     * 이탈리아 Country Fixture
     */
    public static Country italyCountry() {
        return Country.of("IT");
    }

    /**
     * Custom Country Fixture Builder
     */
    public static Country customCountry(String code) {
        return Country.of(code);
    }
}
