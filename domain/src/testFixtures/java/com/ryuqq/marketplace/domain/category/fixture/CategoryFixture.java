package com.ryuqq.marketplace.domain.category.fixture;

import com.ryuqq.marketplace.domain.brand.vo.Department;
import com.ryuqq.marketplace.domain.category.aggregate.Category;
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
 * Category Aggregate Fixture
 *
 * <p>Category Aggregate Root 테스트용 Fixture 클래스</p>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class CategoryFixture {

    private CategoryFixture() {
        // Utility class
    }

    // ==================== 기본 Fixture (신규 생성) ====================

    /**
     * 기본 Root 카테고리 생성 (패션, ACTIVE)
     */
    public static Category defaultRootCategory() {
        return Category.createRoot(
            CategoryCode.of("FASHION"),
            CategoryName.of("패션", "Fashion"),
            Department.FASHION,
            ProductGroup.CLOTHING
        );
    }

    /**
     * 뷰티 Root 카테고리 생성
     */
    public static Category beautyRootCategory() {
        return Category.createRoot(
            CategoryCode.of("BEAUTY"),
            CategoryName.of("뷰티", "Beauty"),
            Department.BEAUTY,
            ProductGroup.BEAUTY
        );
    }

    /**
     * 리빙 Root 카테고리 생성
     */
    public static Category livingRootCategory() {
        return Category.createRoot(
            CategoryCode.of("LIVING"),
            CategoryName.of("리빙", "Living"),
            Department.LIVING,
            ProductGroup.HOME
        );
    }

    /**
     * 디지털 Root 카테고리 생성
     */
    public static Category digitalRootCategory() {
        return Category.createRoot(
            CategoryCode.of("DIGITAL"),
            CategoryName.of("디지털", "Digital"),
            Department.DIGITAL,
            ProductGroup.ELECTRONICS
        );
    }

    // ==================== 계층 구조 Fixture ====================

    /**
     * 하위 카테고리 생성 (부모 필요)
     */
    public static Category childCategory(Category parent, String code, String nameKo, String nameEn) {
        return Category.createChild(
            CategoryCode.of(code),
            CategoryName.of(nameKo, nameEn),
            parent
        );
    }

    /**
     * 의류 하위 카테고리 생성 (2 depth)
     */
    public static Category clothingChildCategory(Category parent) {
        return Category.createChild(
            CategoryCode.of("MEN_CLOTHING"),
            CategoryName.of("남성 의류", "Men's Clothing"),
            parent
        );
    }

    /**
     * 신발 하위 카테고리 생성 (2 depth)
     */
    public static Category shoesChildCategory(Category parent) {
        return Category.createChild(
            CategoryCode.of("SHOES"),
            CategoryName.of("신발", "Shoes"),
            parent
        );
    }

    // ==================== 상태별 Fixture ====================

    /**
     * INACTIVE 상태 카테고리
     */
    public static Category inactiveCategory() {
        Category category = Category.reconstitute(
            CategoryId.of(1L),
            CategoryCode.of("INACTIVE_CAT"),
            CategoryName.of("비활성 카테고리", "Inactive Category"),
            null,
            CategoryDepth.of(0),
            CategoryPath.root(1L),
            SortOrder.defaultOrder(),
            true,
            CategoryStatus.INACTIVE,
            CategoryVisibility.hidden(),
            Department.FASHION,
            ProductGroup.CLOTHING,
            GenderScope.NONE,
            AgeGroup.NONE,
            CategoryMeta.empty(),
            0L
        );
        category.clearEvents();
        return category;
    }

    /**
     * DEPRECATED 상태 카테고리
     */
    public static Category deprecatedCategory() {
        Category category = Category.reconstitute(
            CategoryId.of(2L),
            CategoryCode.of("DEPRECATED_CAT"),
            CategoryName.of("폐기된 카테고리", "Deprecated Category"),
            null,
            CategoryDepth.of(0),
            CategoryPath.root(2L),
            SortOrder.defaultOrder(),
            true,
            CategoryStatus.DEPRECATED,
            CategoryVisibility.hidden(),
            Department.FASHION,
            ProductGroup.CLOTHING,
            GenderScope.NONE,
            AgeGroup.NONE,
            CategoryMeta.empty(),
            0L
        );
        category.clearEvents();
        return category;
    }

    // ==================== 재구성 Fixture (DB에서 로드된 상태) ====================

    /**
     * 재구성된 Root 카테고리 (DB에서 로드된 상태, ACTIVE)
     */
    public static Category reconstitutedRootCategory() {
        return Category.reconstitute(
            CategoryId.of(1L),
            CategoryCode.of("FASHION"),
            CategoryName.of("패션", "Fashion"),
            null,
            CategoryDepth.of(0),
            CategoryPath.root(1L),
            SortOrder.defaultOrder(),
            false, // 하위 카테고리 있음
            CategoryStatus.ACTIVE,
            CategoryVisibility.visible(),
            Department.FASHION,
            ProductGroup.CLOTHING,
            GenderScope.NONE,
            AgeGroup.NONE,
            CategoryMeta.of("패션 카테고리", "fashion", "https://cdn.example.com/icons/fashion.png"),
            1L
        );
    }

    /**
     * 재구성된 하위 카테고리 (depth 1)
     */
    public static Category reconstitutedChildCategory() {
        return Category.reconstitute(
            CategoryId.of(10L),
            CategoryCode.of("MEN_FASHION"),
            CategoryName.of("남성 패션", "Men's Fashion"),
            1L, // parentId
            CategoryDepth.of(1),
            CategoryPath.of("1/10"),
            SortOrder.of(1),
            false, // 하위 카테고리 있음
            CategoryStatus.ACTIVE,
            CategoryVisibility.visible(),
            Department.FASHION,
            ProductGroup.CLOTHING,
            GenderScope.MEN,
            AgeGroup.ADULT,
            CategoryMeta.of("남성 패션", "mens-fashion", "https://cdn.example.com/icons/men.png"),
            1L
        );
    }

    /**
     * 재구성된 Leaf 카테고리 (depth 2)
     */
    public static Category reconstitutedLeafCategory() {
        return Category.reconstitute(
            CategoryId.of(100L),
            CategoryCode.of("MEN_TOPS"),
            CategoryName.of("남성 상의", "Men's Tops"),
            10L, // parentId
            CategoryDepth.of(2),
            CategoryPath.of("1/10/100"),
            SortOrder.of(1),
            true, // Leaf 카테고리
            CategoryStatus.ACTIVE,
            CategoryVisibility.visible(),
            Department.FASHION,
            ProductGroup.CLOTHING,
            GenderScope.MEN,
            AgeGroup.ADULT,
            CategoryMeta.of("남성 상의", "mens-tops", "https://cdn.example.com/icons/tops.png"),
            1L
        );
    }

    /**
     * 완전한 메타데이터를 가진 카테고리
     */
    public static Category categoryWithFullMeta() {
        return Category.reconstitute(
            CategoryId.of(1L),
            CategoryCode.of("FASHION"),
            CategoryName.of("패션", "Fashion"),
            null,
            CategoryDepth.of(0),
            CategoryPath.root(1L),
            SortOrder.of(1),
            false,
            CategoryStatus.ACTIVE,
            CategoryVisibility.visible(),
            Department.FASHION,
            ProductGroup.CLOTHING,
            GenderScope.UNISEX,
            AgeGroup.ADULT,
            CategoryMeta.of("패션 카테고리", "fashion-category", "https://cdn.example.com/icons/fashion.png"),
            1L
        );
    }

    // ==================== 커스텀 빌더 ====================

    /**
     * 커스텀 Root 카테고리 생성 (신규)
     */
    public static Category customRootCategory(
        String code,
        String nameKo,
        String nameEn,
        Department department,
        ProductGroup productGroup
    ) {
        return Category.createRoot(
            CategoryCode.of(code),
            CategoryName.of(nameKo, nameEn),
            department,
            productGroup
        );
    }

    // ==================== Builder Pattern (복잡한 조합용) ====================

    /**
     * Builder 시작
     */
    public static CategoryBuilder builder() {
        return new CategoryBuilder();
    }

    public static class CategoryBuilder {
        private Long id = null;
        private String code = "TEST_CAT";
        private String nameKo = "테스트 카테고리";
        private String nameEn = "Test Category";
        private Long parentId = null;
        private int depth = 0;
        private String path = null;
        private int sortOrder = 0;
        private boolean isLeaf = true;
        private CategoryStatus status = CategoryStatus.ACTIVE;
        private boolean visible = true;
        private boolean listable = true;
        private Department department = Department.FASHION;
        private ProductGroup productGroup = ProductGroup.CLOTHING;
        private GenderScope genderScope = GenderScope.NONE;
        private AgeGroup ageGroup = AgeGroup.NONE;
        private String metaDisplayName = null;
        private String seoSlug = null;
        private String iconUrl = null;
        private long version = 0L;

        public CategoryBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public CategoryBuilder code(String code) {
            this.code = code;
            return this;
        }

        public CategoryBuilder nameKo(String nameKo) {
            this.nameKo = nameKo;
            return this;
        }

        public CategoryBuilder nameEn(String nameEn) {
            this.nameEn = nameEn;
            return this;
        }

        public CategoryBuilder parentId(Long parentId) {
            this.parentId = parentId;
            return this;
        }

        public CategoryBuilder depth(int depth) {
            this.depth = depth;
            return this;
        }

        public CategoryBuilder path(String path) {
            this.path = path;
            return this;
        }

        public CategoryBuilder sortOrder(int sortOrder) {
            this.sortOrder = sortOrder;
            return this;
        }

        public CategoryBuilder isLeaf(boolean isLeaf) {
            this.isLeaf = isLeaf;
            return this;
        }

        public CategoryBuilder status(CategoryStatus status) {
            this.status = status;
            return this;
        }

        public CategoryBuilder visible(boolean visible) {
            this.visible = visible;
            return this;
        }

        public CategoryBuilder listable(boolean listable) {
            this.listable = listable;
            return this;
        }

        public CategoryBuilder department(Department department) {
            this.department = department;
            return this;
        }

        public CategoryBuilder productGroup(ProductGroup productGroup) {
            this.productGroup = productGroup;
            return this;
        }

        public CategoryBuilder genderScope(GenderScope genderScope) {
            this.genderScope = genderScope;
            return this;
        }

        public CategoryBuilder ageGroup(AgeGroup ageGroup) {
            this.ageGroup = ageGroup;
            return this;
        }

        public CategoryBuilder metaDisplayName(String metaDisplayName) {
            this.metaDisplayName = metaDisplayName;
            return this;
        }

        public CategoryBuilder seoSlug(String seoSlug) {
            this.seoSlug = seoSlug;
            return this;
        }

        public CategoryBuilder iconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
            return this;
        }

        public CategoryBuilder version(long version) {
            this.version = version;
            return this;
        }

        /**
         * 신규 Root 카테고리 생성
         */
        public Category createRoot() {
            return Category.createRoot(
                CategoryCode.of(code),
                CategoryName.of(nameKo, nameEn),
                department,
                productGroup
            );
        }

        /**
         * 재구성 카테고리 생성
         */
        public Category reconstitute() {
            CategoryPath categoryPath = path != null ? CategoryPath.of(path) : (id != null ? CategoryPath.root(id) : null);

            return Category.reconstitute(
                id != null ? CategoryId.of(id) : CategoryId.forNew(),
                CategoryCode.of(code),
                CategoryName.of(nameKo, nameEn),
                parentId,
                CategoryDepth.of(depth),
                categoryPath,
                SortOrder.of(sortOrder),
                isLeaf,
                status,
                CategoryVisibility.of(visible, listable),
                department,
                productGroup,
                genderScope,
                ageGroup,
                CategoryMeta.of(metaDisplayName, seoSlug, iconUrl),
                version
            );
        }
    }
}
