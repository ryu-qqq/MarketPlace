package com.ryuqq.fixture.domain;

import com.ryuqq.marketplace.domain.category.vo.CategoryMeta;

/**
 * CategoryMeta Domain 객체 Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public class CategoryMetaFixture {

    private CategoryMetaFixture() {
        throw new AssertionError("Utility class");
    }

    /**
     * 기본 CategoryMeta Fixture
     */
    public static CategoryMeta defaultCategoryMeta() {
        return CategoryMeta.of(
            "의류 카테고리",
            "clothing-category",
            "https://example.com/icons/clothing.png"
        );
    }

    /**
     * 빈 CategoryMeta Fixture
     */
    public static CategoryMeta emptyCategoryMeta() {
        return CategoryMeta.empty();
    }

    /**
     * Custom CategoryMeta Fixture Builder
     */
    public static CategoryMeta customCategoryMeta(String displayName, String seoSlug, String iconUrl) {
        return CategoryMeta.of(displayName, seoSlug, iconUrl);
    }
}
