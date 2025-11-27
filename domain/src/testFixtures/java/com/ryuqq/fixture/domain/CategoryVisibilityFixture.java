package com.ryuqq.fixture.domain;

import com.ryuqq.marketplace.domain.category.vo.CategoryVisibility;

/**
 * CategoryVisibility Domain 객체 Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public class CategoryVisibilityFixture {

    private CategoryVisibilityFixture() {
        throw new AssertionError("Utility class");
    }

    /**
     * 기본 CategoryVisibility Fixture (표시 가능)
     */
    public static CategoryVisibility defaultCategoryVisibility() {
        return CategoryVisibility.visible();
    }

    /**
     * 숨김 CategoryVisibility Fixture
     */
    public static CategoryVisibility hiddenCategoryVisibility() {
        return CategoryVisibility.hidden();
    }

    /**
     * Custom CategoryVisibility Fixture Builder
     */
    public static CategoryVisibility customCategoryVisibility(boolean isVisible, boolean isListable) {
        return CategoryVisibility.of(isVisible, isListable);
    }
}
