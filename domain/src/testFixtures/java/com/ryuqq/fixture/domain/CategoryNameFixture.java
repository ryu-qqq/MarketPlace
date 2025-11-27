package com.ryuqq.fixture.domain;

import com.ryuqq.marketplace.domain.category.vo.CategoryName;

/**
 * CategoryName Domain 객체 Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public class CategoryNameFixture {

    private CategoryNameFixture() {
        throw new AssertionError("Utility class");
    }

    /**
     * 기본 CategoryName Fixture
     */
    public static CategoryName defaultCategoryName() {
        return CategoryName.of("의류", "Clothing");
    }

    /**
     * 한국어만 있는 CategoryName Fixture
     */
    public static CategoryName koreanOnlyCategoryName() {
        return CategoryName.of("의류", null);
    }

    /**
     * 영어만 있는 CategoryName Fixture
     */
    public static CategoryName englishOnlyCategoryName() {
        return CategoryName.of(null, "Clothing");
    }

    /**
     * Custom CategoryName Fixture Builder
     */
    public static CategoryName customCategoryName(String ko, String en) {
        return CategoryName.of(ko, en);
    }
}
