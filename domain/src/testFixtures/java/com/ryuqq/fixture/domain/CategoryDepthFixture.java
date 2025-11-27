package com.ryuqq.fixture.domain;

import com.ryuqq.marketplace.domain.category.vo.CategoryDepth;

/**
 * CategoryDepth Domain 객체 Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public class CategoryDepthFixture {

    private CategoryDepthFixture() {
        throw new AssertionError("Utility class");
    }

    /**
     * 기본 CategoryDepth Fixture (루트 레벨)
     */
    public static CategoryDepth defaultCategoryDepth() {
        return CategoryDepth.of(0);
    }

    /**
     * 1단계 깊이 CategoryDepth Fixture
     */
    public static CategoryDepth firstLevelCategoryDepth() {
        return CategoryDepth.of(1);
    }

    /**
     * 2단계 깊이 CategoryDepth Fixture
     */
    public static CategoryDepth secondLevelCategoryDepth() {
        return CategoryDepth.of(2);
    }

    /**
     * Custom CategoryDepth Fixture Builder
     */
    public static CategoryDepth customCategoryDepth(int value) {
        return CategoryDepth.of(value);
    }
}
