package com.ryuqq.fixture.domain;

import com.ryuqq.marketplace.domain.brand.vo.BrandStatus;

/**
 * BrandStatus Domain 객체 Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public class BrandStatusFixture {

    private BrandStatusFixture() {
        throw new AssertionError("Utility class");
    }

    /**
     * 기본 BrandStatus Fixture (활성)
     */
    public static BrandStatus defaultBrandStatus() {
        return BrandStatus.ACTIVE;
    }

    /**
     * 비활성 BrandStatus Fixture
     */
    public static BrandStatus inactiveBrandStatus() {
        return BrandStatus.INACTIVE;
    }

    /**
     * 차단 BrandStatus Fixture
     */
    public static BrandStatus blockedBrandStatus() {
        return BrandStatus.BLOCKED;
    }
}
