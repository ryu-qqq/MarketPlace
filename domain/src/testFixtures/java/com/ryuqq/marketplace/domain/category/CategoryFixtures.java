package com.ryuqq.marketplace.domain.category;

import com.ryuqq.marketplace.domain.category.aggregate.Category;
import com.ryuqq.marketplace.domain.category.aggregate.CategoryUpdateData;
import com.ryuqq.marketplace.domain.category.id.CategoryId;
import com.ryuqq.marketplace.domain.category.vo.CategoryCode;
import com.ryuqq.marketplace.domain.category.vo.CategoryDepth;
import com.ryuqq.marketplace.domain.category.vo.CategoryGroup;
import com.ryuqq.marketplace.domain.category.vo.CategoryName;
import com.ryuqq.marketplace.domain.category.vo.CategoryPath;
import com.ryuqq.marketplace.domain.category.vo.CategoryStatus;
import com.ryuqq.marketplace.domain.category.vo.Department;
import com.ryuqq.marketplace.domain.category.vo.SortOrder;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import java.time.Instant;

/**
 * Category 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 Category 관련 객체들을 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class CategoryFixtures {

    private CategoryFixtures() {}

    // ===== ID Fixtures =====

    public static CategoryId defaultCategoryId() {
        return CategoryId.of(1L);
    }

    public static CategoryId categoryId(Long value) {
        return CategoryId.of(value);
    }

    // ===== VO Fixtures =====

    public static CategoryCode defaultCategoryCode() {
        return CategoryCode.of("FASHION");
    }

    public static CategoryName defaultCategoryName() {
        return CategoryName.of("패션", "Fashion");
    }

    public static CategoryDepth rootDepth() {
        return CategoryDepth.of(0);
    }

    public static CategoryDepth childDepth() {
        return CategoryDepth.of(1);
    }

    public static CategoryPath rootPath() {
        return CategoryPath.of("1");
    }

    public static CategoryPath childPath() {
        return CategoryPath.of("1/2");
    }

    public static SortOrder defaultSortOrder() {
        return SortOrder.of(1);
    }

    // ===== Aggregate Fixtures =====

    public static Category newCategory() {
        return Category.forNew(
                defaultCategoryCode(),
                defaultCategoryName(),
                null,
                rootDepth(),
                rootPath(),
                defaultSortOrder(),
                Department.FASHION,
                CategoryGroup.CLOTHING,
                null,
                CommonVoFixtures.now());
    }

    public static Category newCategoryWithGroup(CategoryGroup categoryGroup) {
        return Category.forNew(
                defaultCategoryCode(),
                defaultCategoryName(),
                null,
                rootDepth(),
                rootPath(),
                defaultSortOrder(),
                Department.FASHION,
                categoryGroup,
                null,
                CommonVoFixtures.now());
    }

    public static Category activeCategory() {
        Instant yesterday = CommonVoFixtures.yesterday();
        return Category.reconstitute(
                defaultCategoryId(),
                defaultCategoryCode(),
                defaultCategoryName(),
                null,
                rootDepth(),
                rootPath(),
                defaultSortOrder(),
                true,
                CategoryStatus.ACTIVE,
                Department.FASHION,
                CategoryGroup.CLOTHING,
                null,
                null,
                yesterday,
                yesterday);
    }

    public static Category inactiveCategory() {
        Instant yesterday = CommonVoFixtures.yesterday();
        return Category.reconstitute(
                defaultCategoryId(),
                defaultCategoryCode(),
                defaultCategoryName(),
                null,
                rootDepth(),
                rootPath(),
                defaultSortOrder(),
                true,
                CategoryStatus.INACTIVE,
                Department.FASHION,
                CategoryGroup.CLOTHING,
                null,
                null,
                yesterday,
                yesterday);
    }

    public static Category deletedCategory() {
        Instant yesterday = CommonVoFixtures.yesterday();
        Instant now = CommonVoFixtures.now();
        return Category.reconstitute(
                defaultCategoryId(),
                defaultCategoryCode(),
                defaultCategoryName(),
                null,
                rootDepth(),
                rootPath(),
                defaultSortOrder(),
                true,
                CategoryStatus.INACTIVE,
                Department.FASHION,
                CategoryGroup.CLOTHING,
                null,
                now,
                yesterday,
                now);
    }

    public static Category childCategory() {
        Instant yesterday = CommonVoFixtures.yesterday();
        return Category.reconstitute(
                categoryId(2L),
                CategoryCode.of("SHOES"),
                CategoryName.of("신발", "Shoes"),
                1L,
                childDepth(),
                childPath(),
                defaultSortOrder(),
                true,
                CategoryStatus.ACTIVE,
                Department.FASHION,
                CategoryGroup.SHOES,
                null,
                null,
                yesterday,
                yesterday);
    }

    public static Category nonLeafCategory() {
        Instant yesterday = CommonVoFixtures.yesterday();
        return Category.reconstitute(
                defaultCategoryId(),
                defaultCategoryCode(),
                defaultCategoryName(),
                null,
                rootDepth(),
                rootPath(),
                defaultSortOrder(),
                false,
                CategoryStatus.ACTIVE,
                Department.FASHION,
                CategoryGroup.CLOTHING,
                null,
                null,
                yesterday,
                yesterday);
    }

    public static CategoryUpdateData defaultUpdateData() {
        return new CategoryUpdateData(
                CategoryName.of("패션 수정", "Fashion Updated"),
                SortOrder.of(10),
                CategoryStatus.ACTIVE,
                Department.FASHION,
                CategoryGroup.CLOTHING);
    }
}
