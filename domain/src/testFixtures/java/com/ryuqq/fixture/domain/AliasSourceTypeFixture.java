package com.ryuqq.fixture.domain;

import com.ryuqq.marketplace.domain.brand.vo.AliasSourceType;

/**
 * AliasSourceType Domain 객체 Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public class AliasSourceTypeFixture {

    private AliasSourceTypeFixture() {
        throw new AssertionError("Utility class");
    }

    /**
     * 기본 AliasSourceType Fixture (수동)
     */
    public static AliasSourceType defaultAliasSourceType() {
        return AliasSourceType.MANUAL;
    }

    /**
     * 셀러 AliasSourceType Fixture
     */
    public static AliasSourceType sellerAliasSourceType() {
        return AliasSourceType.SELLER;
    }

    /**
     * 외부몰 AliasSourceType Fixture
     */
    public static AliasSourceType externalMallAliasSourceType() {
        return AliasSourceType.EXTERNAL_MALL;
    }

    /**
     * 레거시 AliasSourceType Fixture
     */
    public static AliasSourceType legacyAliasSourceType() {
        return AliasSourceType.LEGACY;
    }

    /**
     * 시스템 AliasSourceType Fixture
     */
    public static AliasSourceType systemAliasSourceType() {
        return AliasSourceType.SYSTEM;
    }
}
