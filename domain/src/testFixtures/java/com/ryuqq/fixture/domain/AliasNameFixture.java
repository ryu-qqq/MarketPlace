package com.ryuqq.fixture.domain;

import com.ryuqq.marketplace.domain.brand.vo.AliasName;

/**
 * AliasName Domain 객체 Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public class AliasNameFixture {

    private AliasNameFixture() {
        throw new AssertionError("Utility class");
    }

    /**
     * 기본 AliasName Fixture
     */
    public static AliasName defaultAliasName() {
        return AliasName.of("Nike Inc.");
    }

    /**
     * Custom AliasName Fixture Builder (자동 정규화)
     */
    public static AliasName customAliasName(String original) {
        return AliasName.of(original);
    }

    /**
     * Custom AliasName Fixture Builder (수동 정규화)
     */
    public static AliasName customAliasName(String original, String normalized) {
        return AliasName.of(original, normalized);
    }
}
