package com.ryuqq.fixture.domain;

import com.ryuqq.marketplace.domain.brand.vo.AliasSource;
import com.ryuqq.marketplace.domain.brand.vo.AliasSourceType;

/**
 * AliasSource Domain 객체 Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public class AliasSourceFixture {

    private AliasSourceFixture() {
        throw new AssertionError("Utility class");
    }

    /**
     * 기본 AliasSource Fixture (수동 입력)
     */
    public static AliasSource defaultAliasSource() {
        return AliasSource.manual();
    }

    /**
     * 셀러 AliasSource Fixture
     */
    public static AliasSource sellerAliasSource() {
        return AliasSource.seller(1L);
    }

    /**
     * 외부몰 AliasSource Fixture
     */
    public static AliasSource externalMallAliasSource() {
        return AliasSource.externalMall("NAVER");
    }

    /**
     * 시스템 AliasSource Fixture
     */
    public static AliasSource systemAliasSource() {
        return AliasSource.system();
    }

    /**
     * Custom AliasSource Fixture Builder
     */
    public static AliasSource customAliasSource(AliasSourceType sourceType, Long sellerId, String mallCode) {
        return AliasSource.of(sourceType, sellerId, mallCode);
    }
}
