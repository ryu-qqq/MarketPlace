package com.ryuqq.fixture.domain;

import com.ryuqq.marketplace.domain.category.vo.CategoryPath;

/**
 * CategoryPath Domain 객체 Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public class CategoryPathFixture {

    private CategoryPathFixture() {
        throw new AssertionError("Utility class");
    }

    /**
     * 기본 CategoryPath Fixture (루트)
     */
    public static CategoryPath defaultCategoryPath() {
        return CategoryPath.root(1L);
    }

    /**
     * 2단계 CategoryPath Fixture
     */
    public static CategoryPath twoLevelCategoryPath() {
        return CategoryPath.of("1/10");
    }

    /**
     * 3단계 CategoryPath Fixture
     */
    public static CategoryPath threeLevelCategoryPath() {
        return CategoryPath.of("1/10/100");
    }

    /**
     * Custom CategoryPath Fixture Builder
     */
    public static CategoryPath customCategoryPath(String path) {
        return CategoryPath.of(path);
    }
}
