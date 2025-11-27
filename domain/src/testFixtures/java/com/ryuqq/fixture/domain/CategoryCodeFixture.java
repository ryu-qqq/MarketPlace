package com.ryuqq.fixture.domain;

import com.ryuqq.marketplace.domain.category.vo.CategoryCode;

/**
 * CategoryCode Domain 객체 Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public class CategoryCodeFixture {

    private CategoryCodeFixture() {
        throw new AssertionError("Utility class");
    }

    /**
     * 기본 CategoryCode Fixture
     */
    public static CategoryCode defaultCategoryCode() {
        return CategoryCode.of("CLOTHING");
    }

    /**
     * Custom CategoryCode Fixture Builder
     */
    public static CategoryCode customCategoryCode(String value) {
        return CategoryCode.of(value);
    }
}
