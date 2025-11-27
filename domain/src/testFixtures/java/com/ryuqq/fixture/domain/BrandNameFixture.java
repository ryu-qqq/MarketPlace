package com.ryuqq.fixture.domain;

import com.ryuqq.marketplace.domain.brand.vo.BrandName;

/**
 * BrandName Domain 객체 Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public class BrandNameFixture {

    private BrandNameFixture() {
        throw new AssertionError("Utility class");
    }

    /**
     * 기본 BrandName Fixture
     */
    public static BrandName defaultBrandName() {
        return BrandName.of("나이키", "Nike", "NK");
    }

    /**
     * 한국어만 있는 BrandName Fixture
     */
    public static BrandName koreanOnlyBrandName() {
        return BrandName.ofKorean("나이키");
    }

    /**
     * 영어만 있는 BrandName Fixture
     */
    public static BrandName englishOnlyBrandName() {
        return BrandName.ofEnglish("Nike");
    }

    /**
     * Custom BrandName Fixture Builder
     */
    public static BrandName customBrandName(String nameKo, String nameEn, String shortName) {
        return BrandName.of(nameKo, nameEn, shortName);
    }
}
