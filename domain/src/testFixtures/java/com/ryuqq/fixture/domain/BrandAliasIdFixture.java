package com.ryuqq.fixture.domain;

import com.ryuqq.marketplace.domain.brand.vo.BrandAliasId;

/**
 * BrandAliasId Domain 객체 Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public class BrandAliasIdFixture {

    private BrandAliasIdFixture() {
        throw new AssertionError("Utility class");
    }

    /**
     * 기본 BrandAliasId Fixture (신규 생성)
     */
    public static BrandAliasId defaultBrandAliasId() {
        return BrandAliasId.forNew();
    }

    /**
     * 기존 BrandAliasId Fixture (저장된 상태)
     */
    public static BrandAliasId defaultExistingBrandAliasId() {
        return BrandAliasId.of(1L);
    }

    /**
     * Custom BrandAliasId Fixture Builder
     */
    public static BrandAliasId customBrandAliasId(Long value) {
        return BrandAliasId.of(value);
    }
}
