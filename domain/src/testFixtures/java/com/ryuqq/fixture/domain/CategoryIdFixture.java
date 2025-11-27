package com.ryuqq.fixture.domain;

import com.ryuqq.marketplace.domain.category.vo.CategoryId;

/**
 * CategoryId Domain 객체 Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public class CategoryIdFixture {

    private CategoryIdFixture() {
        throw new AssertionError("Utility class");
    }

    /**
     * 기본 CategoryId Fixture (신규 생성)
     */
    public static CategoryId defaultCategoryId() {
        return CategoryId.forNew();
    }

    /**
     * 기존 CategoryId Fixture (저장된 상태)
     */
    public static CategoryId defaultExistingCategoryId() {
        return CategoryId.of(1L);
    }

    /**
     * Custom CategoryId Fixture Builder
     */
    public static CategoryId customCategoryId(Long value) {
        return CategoryId.of(value);
    }
}
