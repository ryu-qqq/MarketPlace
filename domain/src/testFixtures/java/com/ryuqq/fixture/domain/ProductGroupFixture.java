package com.ryuqq.fixture.domain;

import com.ryuqq.marketplace.domain.category.vo.ProductGroup;

/**
 * ProductGroup Domain 객체 Test Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public class ProductGroupFixture {

    private ProductGroupFixture() {
        throw new AssertionError("Utility class");
    }

    /**
     * 기본 ProductGroup Fixture (의류)
     */
    public static ProductGroup defaultProductGroup() {
        return ProductGroup.CLOTHING;
    }

    /**
     * 신발 ProductGroup Fixture
     */
    public static ProductGroup shoesProductGroup() {
        return ProductGroup.SHOES;
    }

    /**
     * 가방 ProductGroup Fixture
     */
    public static ProductGroup bagsProductGroup() {
        return ProductGroup.BAGS;
    }

    /**
     * 액세서리 ProductGroup Fixture
     */
    public static ProductGroup accessoriesProductGroup() {
        return ProductGroup.ACCESSORIES;
    }

    /**
     * 주얼리 ProductGroup Fixture
     */
    public static ProductGroup jewelryProductGroup() {
        return ProductGroup.JEWELRY;
    }

    /**
     * 뷰티 ProductGroup Fixture
     */
    public static ProductGroup beautyProductGroup() {
        return ProductGroup.BEAUTY;
    }

    /**
     * 홈/리빙 ProductGroup Fixture
     */
    public static ProductGroup homeProductGroup() {
        return ProductGroup.HOME;
    }

    /**
     * 전자기기 ProductGroup Fixture
     */
    public static ProductGroup electronicsProductGroup() {
        return ProductGroup.ELECTRONICS;
    }

    /**
     * 기타 ProductGroup Fixture
     */
    public static ProductGroup etcProductGroup() {
        return ProductGroup.ETC;
    }
}
