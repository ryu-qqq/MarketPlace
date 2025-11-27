package com.ryuqq.fixture.domain;

import com.ryuqq.marketplace.domain.brand.vo.BrandMeta;

/**
 * BrandMeta Domain 객체 Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public class BrandMetaFixture {

    private BrandMetaFixture() {
        throw new AssertionError("Utility class");
    }

    /**
     * 기본 BrandMeta Fixture
     */
    public static BrandMeta defaultBrandMeta() {
        return BrandMeta.of(
            "https://www.nike.com",
            "https://www.nike.com/logo.png",
            "세계적인 스포츠 브랜드"
        );
    }

    /**
     * 빈 BrandMeta Fixture
     */
    public static BrandMeta emptyBrandMeta() {
        return BrandMeta.empty();
    }

    /**
     * Custom BrandMeta Fixture Builder
     */
    public static BrandMeta customBrandMeta(String officialWebsite, String logoUrl, String description) {
        return BrandMeta.of(officialWebsite, logoUrl, description);
    }
}
