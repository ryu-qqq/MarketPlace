package com.ryuqq.marketplace.domain.category.aggregate;

import com.ryuqq.marketplace.domain.brand.vo.Department;
import com.ryuqq.marketplace.domain.category.event.CategoryCreatedEvent;
import com.ryuqq.marketplace.domain.category.event.CategoryStatusChangedEvent;
import com.ryuqq.marketplace.domain.category.event.CategoryUpdatedEvent;
import com.ryuqq.marketplace.domain.category.fixture.CategoryFixture;
import com.ryuqq.marketplace.domain.category.fixture.CategoryVoFixture;
import com.ryuqq.marketplace.domain.category.vo.AgeGroup;
import com.ryuqq.marketplace.domain.category.vo.CategoryCode;
import com.ryuqq.marketplace.domain.category.vo.CategoryMeta;
import com.ryuqq.marketplace.domain.category.vo.CategoryName;
import com.ryuqq.marketplace.domain.category.vo.CategoryStatus;
import com.ryuqq.marketplace.domain.category.vo.CategoryVisibility;
import com.ryuqq.marketplace.domain.category.vo.GenderScope;
import com.ryuqq.marketplace.domain.category.vo.ProductGroup;
import com.ryuqq.marketplace.domain.category.vo.SortOrder;
import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Category Aggregate Root 단위 테스트
 *
 * <p><strong>테스트 범위</strong>:</p>
 * <ul>
 *   <li>생성 테스트 - createRoot(), createChild(), reconstitute()</li>
 *   <li>도메인 행위 테스트 - updateName, changeStatus, updateVisibility 등</li>
 *   <li>쿼리 메서드 테스트 - isRoot(), isLeaf(), isActive() 등</li>
 *   <li>Getter 테스트 - Law of Demeter 준수 확인</li>
 *   <li>도메인 이벤트 테스트</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("domain")
@Tag("unit")
@Tag("category")
@DisplayName("Category Aggregate 단위 테스트")
class CategoryTest {

    // ==================== 생성 테스트 ====================

    @Nested
    @DisplayName("생성 테스트")
    class CreateTest {

        @Test
        @DisplayName("[성공] Category.createRoot()로 루트 카테고리 생성")
        void createRoot_ShouldCreateRootCategory() {
            // given
            CategoryCode code = CategoryVoFixture.categoryCode("FASHION");
            CategoryName name = CategoryVoFixture.categoryName();
            Department department = CategoryVoFixture.departmentFashion();
            ProductGroup productGroup = CategoryVoFixture.productGroupClothing();

            // when
            Category category = Category.createRoot(code, name, department, productGroup);

            // then
            assertThat(category.id().isNew()).isTrue();
            assertThat(category.codeValue()).isEqualTo("FASHION");
            assertThat(category.displayName()).isEqualTo("패션");
            assertThat(category.isRoot()).isTrue();
            assertThat(category.isLeaf()).isTrue(); // 초기값
            assertThat(category.status()).isEqualTo(CategoryStatus.ACTIVE);
            assertThat(category.depthValue()).isZero();
            assertThat(category.parentIdValue()).isNull();
            assertThat(category.version()).isZero();
        }

        @Test
        @DisplayName("[성공] Category.createRoot() 시 CategoryCreatedEvent 발행")
        void createRoot_ShouldPublishCategoryCreatedEvent() {
            // when
            Category category = CategoryFixture.defaultRootCategory();

            // then
            assertThat(category.domainEvents()).hasSize(1);
            assertThat(category.domainEvents().get(0)).isInstanceOf(CategoryCreatedEvent.class);
        }

        @Test
        @DisplayName("[성공] Category.createChild()로 하위 카테고리 생성")
        void createChild_ShouldCreateChildCategory() {
            // given
            Category parent = CategoryFixture.reconstitutedRootCategory();

            // when
            Category child = Category.createChild(
                CategoryCode.of("MEN_FASHION"),
                CategoryName.of("남성 패션", "Men's Fashion"),
                parent
            );

            // then
            assertThat(child.id().isNew()).isTrue();
            assertThat(child.codeValue()).isEqualTo("MEN_FASHION");
            assertThat(child.isRoot()).isFalse();
            assertThat(child.parentIdValue()).isEqualTo(parent.idValue());
            assertThat(child.depthValue()).isEqualTo(parent.depthValue() + 1);
            assertThat(child.department()).isEqualTo(parent.department());
            assertThat(child.productGroup()).isEqualTo(parent.productGroup());
        }

        @Test
        @DisplayName("[성공] Category.createChild() 시 CategoryCreatedEvent 발행")
        void createChild_ShouldPublishCategoryCreatedEvent() {
            // given
            Category parent = CategoryFixture.reconstitutedRootCategory();

            // when
            Category child = Category.createChild(
                CategoryCode.of("CHILD_CAT"),
                CategoryName.of("하위", "Child"),
                parent
            );

            // then
            assertThat(child.domainEvents()).hasSize(1);
            assertThat(child.domainEvents().get(0)).isInstanceOf(CategoryCreatedEvent.class);
        }

        @Test
        @DisplayName("[성공] Category.reconstitute()로 영속성에서 복원")
        void reconstitute_ShouldReconstitute() {
            // when
            Category category = CategoryFixture.reconstitutedRootCategory();

            // then
            assertThat(category.idValue()).isEqualTo(1L);
            assertThat(category.codeValue()).isEqualTo("FASHION");
            assertThat(category.version()).isEqualTo(1L);
            assertThat(category.domainEvents()).isEmpty(); // 재구성 시 이벤트 없음
        }

        @Test
        @DisplayName("[성공] 다양한 부서와 상품 그룹으로 루트 카테고리 생성")
        void createRoot_WithDifferentDepartments_ShouldCreate() {
            // when
            Category beauty = CategoryFixture.beautyRootCategory();
            Category living = CategoryFixture.livingRootCategory();
            Category digital = CategoryFixture.digitalRootCategory();

            // then
            assertThat(beauty.department()).isEqualTo(Department.BEAUTY);
            assertThat(beauty.productGroup()).isEqualTo(ProductGroup.BEAUTY);

            assertThat(living.department()).isEqualTo(Department.LIVING);
            assertThat(living.productGroup()).isEqualTo(ProductGroup.HOME);

            assertThat(digital.department()).isEqualTo(Department.DIGITAL);
            assertThat(digital.productGroup()).isEqualTo(ProductGroup.ELECTRONICS);
        }
    }

    // ==================== 도메인 행위 테스트 ====================

    @Nested
    @DisplayName("도메인 행위 테스트")
    class DomainBehaviorTest {

        @Test
        @DisplayName("[성공] updateName()으로 카테고리 이름 변경")
        void updateName_ShouldUpdateName() {
            // given
            Category category = CategoryFixture.reconstitutedRootCategory();
            category.clearEvents();

            CategoryName newName = CategoryName.of("새 패션", "New Fashion");

            // when
            category.updateName(newName);

            // then
            assertThat(category.nameKo()).isEqualTo("새 패션");
            assertThat(category.nameEn()).isEqualTo("New Fashion");
        }

        @Test
        @DisplayName("[성공] updateName() 시 CategoryUpdatedEvent 발행")
        void updateName_ShouldPublishCategoryUpdatedEvent() {
            // given
            Category category = CategoryFixture.reconstitutedRootCategory();
            category.clearEvents();

            // when
            category.updateName(CategoryName.of("변경", "Changed"));

            // then
            assertThat(category.domainEvents()).hasSize(1);
            assertThat(category.domainEvents().get(0)).isInstanceOf(CategoryUpdatedEvent.class);
        }

        @Test
        @DisplayName("[성공] changeStatus()로 상태 변경")
        void changeStatus_ShouldChangeStatus() {
            // given
            Category category = CategoryFixture.reconstitutedRootCategory();
            category.clearEvents();

            // when
            category.changeStatus(CategoryStatus.INACTIVE);

            // then
            assertThat(category.status()).isEqualTo(CategoryStatus.INACTIVE);
        }

        @Test
        @DisplayName("[성공] changeStatus() 시 CategoryStatusChangedEvent 발행")
        void changeStatus_ShouldPublishCategoryStatusChangedEvent() {
            // given
            Category category = CategoryFixture.reconstitutedRootCategory();
            category.clearEvents();

            // when
            category.changeStatus(CategoryStatus.DEPRECATED);

            // then
            assertThat(category.domainEvents()).hasSize(1);
            DomainEvent event = category.domainEvents().get(0);
            assertThat(event).isInstanceOf(CategoryStatusChangedEvent.class);
        }

        @Test
        @DisplayName("[성공] updateSortOrder()로 정렬 순서 변경")
        void updateSortOrder_ShouldUpdateSortOrder() {
            // given
            Category category = CategoryFixture.reconstitutedRootCategory();

            // when
            category.updateSortOrder(SortOrder.of(10));

            // then
            assertThat(category.sortOrderValue()).isEqualTo(10);
        }

        @Test
        @DisplayName("[성공] updateVisibility()로 표시 설정 변경")
        void updateVisibility_ShouldUpdateVisibility() {
            // given
            Category category = CategoryFixture.reconstitutedRootCategory();
            category.clearEvents();

            // when
            category.updateVisibility(CategoryVisibility.hidden());

            // then
            assertThat(category.isVisible()).isFalse();
            assertThat(category.isListable()).isFalse();
        }

        @Test
        @DisplayName("[성공] updateVisibility() 시 CategoryUpdatedEvent 발행")
        void updateVisibility_ShouldPublishCategoryUpdatedEvent() {
            // given
            Category category = CategoryFixture.reconstitutedRootCategory();
            category.clearEvents();

            // when
            category.updateVisibility(CategoryVisibility.hidden());

            // then
            assertThat(category.domainEvents()).hasSize(1);
            assertThat(category.domainEvents().get(0)).isInstanceOf(CategoryUpdatedEvent.class);
        }

        @Test
        @DisplayName("[성공] updateMeta()로 메타데이터 변경")
        void updateMeta_ShouldUpdateMeta() {
            // given
            Category category = CategoryFixture.reconstitutedRootCategory();

            CategoryMeta newMeta = CategoryMeta.of("새 표시명", "new-slug", "https://cdn.example.com/new-icon.png");

            // when
            category.updateMeta(newMeta);

            // then
            assertThat(category.metaDisplayName()).isEqualTo("새 표시명");
            assertThat(category.seoSlug()).isEqualTo("new-slug");
            assertThat(category.iconUrl()).isEqualTo("https://cdn.example.com/new-icon.png");
        }

        @Test
        @DisplayName("[성공] updateBusinessInfo()로 비즈니스 정보 변경")
        void updateBusinessInfo_ShouldUpdateBusinessInfo() {
            // given
            Category category = CategoryFixture.reconstitutedRootCategory();
            category.clearEvents();

            // when
            category.updateBusinessInfo(
                Department.BEAUTY,
                ProductGroup.BEAUTY,
                GenderScope.WOMEN,
                AgeGroup.ADULT
            );

            // then
            assertThat(category.department()).isEqualTo(Department.BEAUTY);
            assertThat(category.productGroup()).isEqualTo(ProductGroup.BEAUTY);
            assertThat(category.genderScope()).isEqualTo(GenderScope.WOMEN);
            assertThat(category.ageGroup()).isEqualTo(AgeGroup.ADULT);
        }

        @Test
        @DisplayName("[성공] updateBusinessInfo() 시 CategoryUpdatedEvent 발행")
        void updateBusinessInfo_ShouldPublishCategoryUpdatedEvent() {
            // given
            Category category = CategoryFixture.reconstitutedRootCategory();
            category.clearEvents();

            // when
            category.updateBusinessInfo(
                Department.DIGITAL,
                ProductGroup.ELECTRONICS,
                GenderScope.UNISEX,
                AgeGroup.ADULT
            );

            // then
            assertThat(category.domainEvents()).hasSize(1);
            assertThat(category.domainEvents().get(0)).isInstanceOf(CategoryUpdatedEvent.class);
        }

        @Test
        @DisplayName("[성공] markAsLeaf()로 리프 카테고리로 표시")
        void markAsLeaf_ShouldMarkAsLeaf() {
            // given
            Category category = CategoryFixture.reconstitutedRootCategory();
            assertThat(category.isLeaf()).isFalse();

            // when
            category.markAsLeaf();

            // then
            assertThat(category.isLeaf()).isTrue();
        }

        @Test
        @DisplayName("[성공] markAsNotLeaf()로 비리프 카테고리로 표시")
        void markAsNotLeaf_ShouldMarkAsNotLeaf() {
            // given
            Category category = CategoryFixture.reconstitutedLeafCategory();
            assertThat(category.isLeaf()).isTrue();

            // when
            category.markAsNotLeaf();

            // then
            assertThat(category.isLeaf()).isFalse();
        }
    }

    // ==================== 쿼리 메서드 테스트 ====================

    @Nested
    @DisplayName("쿼리 메서드 테스트")
    class QueryMethodTest {

        @Test
        @DisplayName("[성공] isRoot()는 parentId가 null일 때 true")
        void isRoot_WhenParentIdNull_ShouldReturnTrue() {
            // given
            Category root = CategoryFixture.reconstitutedRootCategory();

            // when & then
            assertThat(root.isRoot()).isTrue();
        }

        @Test
        @DisplayName("[성공] isRoot()는 parentId가 있을 때 false")
        void isRoot_WhenParentIdExists_ShouldReturnFalse() {
            // given
            Category child = CategoryFixture.reconstitutedChildCategory();

            // when & then
            assertThat(child.isRoot()).isFalse();
        }

        @Test
        @DisplayName("[성공] isLeaf()는 리프 카테고리일 때 true")
        void isLeaf_WhenLeafCategory_ShouldReturnTrue() {
            // given
            Category leaf = CategoryFixture.reconstitutedLeafCategory();

            // when & then
            assertThat(leaf.isLeaf()).isTrue();
        }

        @Test
        @DisplayName("[성공] isLeaf()는 하위 카테고리가 있을 때 false")
        void isLeaf_WhenNotLeafCategory_ShouldReturnFalse() {
            // given
            Category nonLeaf = CategoryFixture.reconstitutedChildCategory();

            // when & then
            assertThat(nonLeaf.isLeaf()).isFalse();
        }

        @Test
        @DisplayName("[성공] isActive()는 ACTIVE 상태일 때 true")
        void isActive_WhenActiveStatus_ShouldReturnTrue() {
            // given
            Category category = CategoryFixture.reconstitutedRootCategory();

            // when & then
            assertThat(category.isActive()).isTrue();
        }

        @Test
        @DisplayName("[성공] isActive()는 INACTIVE 상태일 때 false")
        void isActive_WhenInactiveStatus_ShouldReturnFalse() {
            // given
            Category category = CategoryFixture.inactiveCategory();

            // when & then
            assertThat(category.isActive()).isFalse();
        }

        @Test
        @DisplayName("[성공] isActive()는 DEPRECATED 상태일 때 false")
        void isActive_WhenDeprecatedStatus_ShouldReturnFalse() {
            // given
            Category category = CategoryFixture.deprecatedCategory();

            // when & then
            assertThat(category.isActive()).isFalse();
        }

        @Test
        @DisplayName("[성공] isVisible()는 visible=true일 때 true")
        void isVisible_WhenVisibleTrue_ShouldReturnTrue() {
            // given
            Category category = CategoryFixture.reconstitutedRootCategory();

            // when & then
            assertThat(category.isVisible()).isTrue();
        }

        @Test
        @DisplayName("[성공] isVisible()는 hidden 상태일 때 false")
        void isVisible_WhenHidden_ShouldReturnFalse() {
            // given
            Category category = CategoryFixture.inactiveCategory();

            // when & then
            assertThat(category.isVisible()).isFalse();
        }

        @Test
        @DisplayName("[성공] isListable()는 listable=true일 때 true")
        void isListable_WhenListableTrue_ShouldReturnTrue() {
            // given
            Category category = CategoryFixture.reconstitutedRootCategory();

            // when & then
            assertThat(category.isListable()).isTrue();
        }
    }

    // ==================== Getter 테스트 ====================

    @Nested
    @DisplayName("Getter 테스트 (Law of Demeter 준수)")
    class GetterTest {

        @Test
        @DisplayName("[성공] nameKo()는 CategoryName.ko()를 직접 반환")
        void nameKo_ShouldReturnDirectValue() {
            // given
            Category category = CategoryFixture.reconstitutedRootCategory();

            // when & then
            assertThat(category.nameKo()).isEqualTo("패션");
        }

        @Test
        @DisplayName("[성공] nameEn()는 CategoryName.en()를 직접 반환")
        void nameEn_ShouldReturnDirectValue() {
            // given
            Category category = CategoryFixture.reconstitutedRootCategory();

            // when & then
            assertThat(category.nameEn()).isEqualTo("Fashion");
        }

        @Test
        @DisplayName("[성공] displayName()은 우선순위에 따라 반환 (ko > en)")
        void displayName_ShouldReturnByPriority() {
            // given
            Category koreanCategory = CategoryFixture.reconstitutedRootCategory();
            Category englishOnlyCategory = CategoryFixture.builder()
                .id(2L)
                .code("EN_ONLY")
                .nameKo(null)
                .nameEn("English Only")
                .path("2")
                .reconstitute();

            // when & then
            assertThat(koreanCategory.displayName()).isEqualTo("패션");
            assertThat(englishOnlyCategory.displayName()).isEqualTo("English Only");
        }

        @Test
        @DisplayName("[성공] codeValue()는 CategoryCode.value()를 직접 반환")
        void codeValue_ShouldReturnDirectValue() {
            // given
            Category category = CategoryFixture.reconstitutedRootCategory();

            // when & then
            assertThat(category.codeValue()).isEqualTo("FASHION");
        }

        @Test
        @DisplayName("[성공] pathValue()는 CategoryPath.value()를 직접 반환")
        void pathValue_ShouldReturnDirectValue() {
            // given
            Category category = CategoryFixture.reconstitutedChildCategory();

            // when & then
            assertThat(category.pathValue()).isEqualTo("1/10");
        }

        @Test
        @DisplayName("[성공] metaDisplayName()는 CategoryMeta.displayName()를 직접 반환")
        void metaDisplayName_ShouldReturnDirectValue() {
            // given
            Category category = CategoryFixture.categoryWithFullMeta();

            // when & then
            assertThat(category.metaDisplayName()).isEqualTo("패션 카테고리");
        }

        @Test
        @DisplayName("[성공] seoSlug()는 CategoryMeta.seoSlug()를 직접 반환")
        void seoSlug_ShouldReturnDirectValue() {
            // given
            Category category = CategoryFixture.categoryWithFullMeta();

            // when & then
            assertThat(category.seoSlug()).isEqualTo("fashion-category");
        }

        @Test
        @DisplayName("[성공] iconUrl()는 CategoryMeta.iconUrl()를 직접 반환")
        void iconUrl_ShouldReturnDirectValue() {
            // given
            Category category = CategoryFixture.categoryWithFullMeta();

            // when & then
            assertThat(category.iconUrl()).isEqualTo("https://cdn.example.com/icons/fashion.png");
        }

        @Test
        @DisplayName("[성공] domainEvents()는 불변 리스트 반환")
        void domainEvents_ShouldReturnUnmodifiableList() {
            // given
            Category category = CategoryFixture.defaultRootCategory();

            // when & then
            assertThatThrownBy(() -> category.domainEvents().clear())
                .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    // ==================== 도메인 이벤트 관리 테스트 ====================

    @Nested
    @DisplayName("도메인 이벤트 관리 테스트")
    class DomainEventTest {

        @Test
        @DisplayName("[성공] clearEvents()로 이벤트 목록 초기화")
        void clearEvents_ShouldClearEvents() {
            // given
            Category category = CategoryFixture.defaultRootCategory();
            assertThat(category.domainEvents()).isNotEmpty();

            // when
            category.clearEvents();

            // then
            assertThat(category.domainEvents()).isEmpty();
        }

        @Test
        @DisplayName("[성공] 여러 행위 수행 시 이벤트 누적")
        void multipleActions_ShouldAccumulateEvents() {
            // given
            Category category = CategoryFixture.reconstitutedRootCategory();
            category.clearEvents();

            // when
            category.updateName(CategoryName.of("이름변경", "Changed"));
            category.changeStatus(CategoryStatus.INACTIVE);

            // then
            assertThat(category.domainEvents()).hasSize(2);
            assertThat(category.domainEvents().get(0)).isInstanceOf(CategoryUpdatedEvent.class);
            assertThat(category.domainEvents().get(1)).isInstanceOf(CategoryStatusChangedEvent.class);
        }
    }

    // ==================== 계층 구조 테스트 ====================

    @Nested
    @DisplayName("계층 구조 테스트")
    class HierarchyTest {

        @Test
        @DisplayName("[성공] 3단계 계층 구조 생성")
        void createHierarchy_ShouldCreateThreeLevels() {
            // given
            Category root = CategoryFixture.reconstitutedRootCategory();
            Category child = CategoryFixture.reconstitutedChildCategory();
            Category leaf = CategoryFixture.reconstitutedLeafCategory();

            // then
            assertThat(root.depthValue()).isZero();
            assertThat(child.depthValue()).isEqualTo(1);
            assertThat(leaf.depthValue()).isEqualTo(2);

            assertThat(root.isRoot()).isTrue();
            assertThat(child.isRoot()).isFalse();
            assertThat(leaf.isRoot()).isFalse();

            assertThat(root.isLeaf()).isFalse();
            assertThat(child.isLeaf()).isFalse();
            assertThat(leaf.isLeaf()).isTrue();
        }

        @Test
        @DisplayName("[성공] 하위 카테고리는 부모의 department와 productGroup 상속")
        void createChild_ShouldInheritParentProperties() {
            // given
            Category parent = CategoryFixture.reconstitutedRootCategory();

            // when
            Category child = Category.createChild(
                CategoryCode.of("CHILD"),
                CategoryName.of("자식", "Child"),
                parent
            );

            // then
            assertThat(child.department()).isEqualTo(parent.department());
            assertThat(child.productGroup()).isEqualTo(parent.productGroup());
            assertThat(child.genderScope()).isEqualTo(parent.genderScope());
            assertThat(child.ageGroup()).isEqualTo(parent.ageGroup());
        }

        @Test
        @DisplayName("[성공] 경로 구조 확인")
        void pathStructure_ShouldBeCorrect() {
            // given
            Category root = CategoryFixture.reconstitutedRootCategory();
            Category child = CategoryFixture.reconstitutedChildCategory();
            Category leaf = CategoryFixture.reconstitutedLeafCategory();

            // then
            assertThat(root.pathValue()).isEqualTo("1");
            assertThat(child.pathValue()).isEqualTo("1/10");
            assertThat(leaf.pathValue()).isEqualTo("1/10/100");
        }
    }

    // ==================== Builder 테스트 ====================

    @Nested
    @DisplayName("Builder 테스트")
    class BuilderTest {

        @Test
        @DisplayName("[성공] Builder로 신규 루트 카테고리 생성")
        void builder_CreateRoot_ShouldWork() {
            // when
            Category category = CategoryFixture.builder()
                .code("BUILDER_TEST")
                .nameKo("빌더 테스트")
                .nameEn("Builder Test")
                .department(Department.FASHION)
                .productGroup(ProductGroup.CLOTHING)
                .createRoot();

            // then
            assertThat(category.codeValue()).isEqualTo("BUILDER_TEST");
            assertThat(category.nameKo()).isEqualTo("빌더 테스트");
            assertThat(category.isRoot()).isTrue();
        }

        @Test
        @DisplayName("[성공] Builder로 재구성 카테고리 생성")
        void builder_Reconstitute_ShouldWork() {
            // when
            Category category = CategoryFixture.builder()
                .id(999L)
                .code("RECONSTITUTED")
                .nameKo("재구성")
                .nameEn("Reconstituted")
                .path("999")
                .status(CategoryStatus.INACTIVE)
                .visible(false)
                .listable(false)
                .version(5L)
                .reconstitute();

            // then
            assertThat(category.idValue()).isEqualTo(999L);
            assertThat(category.status()).isEqualTo(CategoryStatus.INACTIVE);
            assertThat(category.isVisible()).isFalse();
            assertThat(category.version()).isEqualTo(5L);
        }
    }
}
