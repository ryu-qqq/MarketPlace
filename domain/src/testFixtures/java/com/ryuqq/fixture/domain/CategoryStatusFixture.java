package com.ryuqq.fixture.domain;

import com.ryuqq.marketplace.domain.category.vo.CategoryStatus;

/**
 * CategoryStatus Domain 객체 Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public class CategoryStatusFixture {

    private CategoryStatusFixture() {
        throw new AssertionError("Utility class");
    }

    /**
     * 기본 CategoryStatus Fixture (활성)
     */
    public static CategoryStatus defaultCategoryStatus() {
        return CategoryStatus.ACTIVE;
    }

    /**
     * 비활성 CategoryStatus Fixture
     */
    public static CategoryStatus inactiveCategoryStatus() {
        return CategoryStatus.INACTIVE;
    }

    /**
     * 폐기 CategoryStatus Fixture
     */
    public static CategoryStatus deprecatedCategoryStatus() {
        return CategoryStatus.DEPRECATED;
    }
}
