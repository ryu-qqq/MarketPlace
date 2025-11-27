package com.ryuqq.fixture.domain;

import com.ryuqq.marketplace.domain.category.vo.SortOrder;

/**
 * SortOrder Domain 객체 Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public class SortOrderFixture {

    private SortOrderFixture() {
        throw new AssertionError("Utility class");
    }

    /**
     * 기본 SortOrder Fixture
     */
    public static SortOrder defaultSortOrder() {
        return SortOrder.defaultOrder();
    }

    /**
     * Custom SortOrder Fixture Builder
     */
    public static SortOrder customSortOrder(int value) {
        return SortOrder.of(value);
    }
}
