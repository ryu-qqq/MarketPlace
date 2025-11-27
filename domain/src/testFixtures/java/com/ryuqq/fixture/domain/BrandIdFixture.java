package com.ryuqq.fixture.domain;

import com.ryuqq.marketplace.domain.brand.vo.BrandId;

/**
 * BrandId Domain 객체 Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public class BrandIdFixture {

    private BrandIdFixture() {
        throw new AssertionError("Utility class");
    }

    /**
     * 기본 BrandId Fixture (신규 생성)
     */
    public static BrandId defaultBrandId() {
        return BrandId.forNew();
    }

    /**
     * 기존 BrandId Fixture (저장된 상태)
     */
    public static BrandId defaultExistingBrandId() {
        return BrandId.of(1L);
    }

    /**
     * Custom BrandId Fixture Builder
     */
    public static BrandId customBrandId(Long value) {
        return BrandId.of(value);
    }
}
