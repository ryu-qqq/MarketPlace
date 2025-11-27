package com.ryuqq.marketplace.domain.category.fixture;

import com.ryuqq.marketplace.domain.brand.vo.Department;
import com.ryuqq.marketplace.domain.category.vo.AgeGroup;
import com.ryuqq.marketplace.domain.category.vo.CategoryCode;
import com.ryuqq.marketplace.domain.category.vo.CategoryDepth;
import com.ryuqq.marketplace.domain.category.vo.CategoryId;
import com.ryuqq.marketplace.domain.category.vo.CategoryMeta;
import com.ryuqq.marketplace.domain.category.vo.CategoryName;
import com.ryuqq.marketplace.domain.category.vo.CategoryPath;
import com.ryuqq.marketplace.domain.category.vo.CategoryStatus;
import com.ryuqq.marketplace.domain.category.vo.CategoryVisibility;
import com.ryuqq.marketplace.domain.category.vo.GenderScope;
import com.ryuqq.marketplace.domain.category.vo.ProductGroup;
import com.ryuqq.marketplace.domain.category.vo.SortOrder;

/**
 * Category VO Fixture
 *
 * <p>Category 도메인의 Value Object 테스트용 Fixture 클래스</p>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class CategoryVoFixture {

    private CategoryVoFixture() {
        // Utility class
    }

    // ==================== CategoryId ====================

    public static CategoryId categoryId() {
        return CategoryId.of(1L);
    }

    public static CategoryId categoryId(Long value) {
        return CategoryId.of(value);
    }

    public static CategoryId newCategoryId() {
        return CategoryId.forNew();
    }

    // ==================== CategoryCode ====================

    public static CategoryCode categoryCode() {
        return CategoryCode.of("FASHION");
    }

    public static CategoryCode categoryCode(String value) {
        return CategoryCode.of(value);
    }

    // ==================== CategoryName ====================

    public static CategoryName categoryName() {
        return CategoryName.of("패션", "Fashion");
    }

    public static CategoryName categoryNameKorean() {
        return CategoryName.of("패션", null);
    }

    public static CategoryName categoryNameEnglish() {
        return CategoryName.of(null, "Fashion");
    }

    public static CategoryName categoryName(String ko, String en) {
        return CategoryName.of(ko, en);
    }

    // ==================== CategoryDepth ====================

    public static CategoryDepth depthRoot() {
        return CategoryDepth.of(0);
    }

    public static CategoryDepth depthLevel1() {
        return CategoryDepth.of(1);
    }

    public static CategoryDepth depthLevel2() {
        return CategoryDepth.of(2);
    }

    public static CategoryDepth depth(int value) {
        return CategoryDepth.of(value);
    }

    // ==================== CategoryPath ====================

    public static CategoryPath pathRoot() {
        return CategoryPath.root(1L);
    }

    public static CategoryPath pathRoot(Long id) {
        return CategoryPath.root(id);
    }

    public static CategoryPath path(String value) {
        return CategoryPath.of(value);
    }

    public static CategoryPath pathHierarchy() {
        return CategoryPath.of("1/10/100");
    }

    // ==================== SortOrder ====================

    public static SortOrder sortOrderDefault() {
        return SortOrder.defaultOrder();
    }

    public static SortOrder sortOrder(int value) {
        return SortOrder.of(value);
    }

    // ==================== CategoryStatus ====================

    public static CategoryStatus statusActive() {
        return CategoryStatus.ACTIVE;
    }

    public static CategoryStatus statusInactive() {
        return CategoryStatus.INACTIVE;
    }

    public static CategoryStatus statusDeprecated() {
        return CategoryStatus.DEPRECATED;
    }

    // ==================== CategoryVisibility ====================

    public static CategoryVisibility visibilityVisible() {
        return CategoryVisibility.visible();
    }

    public static CategoryVisibility visibilityHidden() {
        return CategoryVisibility.hidden();
    }

    public static CategoryVisibility visibility(boolean visible, boolean listable) {
        return CategoryVisibility.of(visible, listable);
    }

    // ==================== ProductGroup ====================

    public static ProductGroup productGroupClothing() {
        return ProductGroup.CLOTHING;
    }

    public static ProductGroup productGroupShoes() {
        return ProductGroup.SHOES;
    }

    public static ProductGroup productGroupBags() {
        return ProductGroup.BAGS;
    }

    public static ProductGroup productGroupAccessories() {
        return ProductGroup.ACCESSORIES;
    }

    public static ProductGroup productGroupJewelry() {
        return ProductGroup.JEWELRY;
    }

    public static ProductGroup productGroupBeauty() {
        return ProductGroup.BEAUTY;
    }

    public static ProductGroup productGroupHome() {
        return ProductGroup.HOME;
    }

    public static ProductGroup productGroupElectronics() {
        return ProductGroup.ELECTRONICS;
    }

    public static ProductGroup productGroupEtc() {
        return ProductGroup.ETC;
    }

    // ==================== GenderScope ====================

    public static GenderScope genderMen() {
        return GenderScope.MEN;
    }

    public static GenderScope genderWomen() {
        return GenderScope.WOMEN;
    }

    public static GenderScope genderUnisex() {
        return GenderScope.UNISEX;
    }

    public static GenderScope genderKids() {
        return GenderScope.KIDS;
    }

    public static GenderScope genderNone() {
        return GenderScope.NONE;
    }

    // ==================== AgeGroup ====================

    public static AgeGroup ageInfant() {
        return AgeGroup.INFANT;
    }

    public static AgeGroup ageKids() {
        return AgeGroup.KIDS;
    }

    public static AgeGroup ageTeen() {
        return AgeGroup.TEEN;
    }

    public static AgeGroup ageAdult() {
        return AgeGroup.ADULT;
    }

    public static AgeGroup ageSenior() {
        return AgeGroup.SENIOR;
    }

    public static AgeGroup ageNone() {
        return AgeGroup.NONE;
    }

    // ==================== CategoryMeta ====================

    public static CategoryMeta categoryMeta() {
        return CategoryMeta.of(
            "패션 카테고리",
            "fashion",
            "https://cdn.example.com/icons/fashion.png"
        );
    }

    public static CategoryMeta categoryMetaEmpty() {
        return CategoryMeta.empty();
    }

    public static CategoryMeta categoryMeta(String displayName, String seoSlug, String iconUrl) {
        return CategoryMeta.of(displayName, seoSlug, iconUrl);
    }

    // ==================== Department (Brand VO 재사용) ====================

    public static Department departmentFashion() {
        return Department.FASHION;
    }

    public static Department departmentBeauty() {
        return Department.BEAUTY;
    }

    public static Department departmentLiving() {
        return Department.LIVING;
    }

    public static Department departmentDigital() {
        return Department.DIGITAL;
    }

    public static Department departmentEtc() {
        return Department.ETC;
    }
}
