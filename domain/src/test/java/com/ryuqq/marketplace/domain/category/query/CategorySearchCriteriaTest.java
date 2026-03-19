package com.ryuqq.marketplace.domain.category.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.category.vo.CategoryGroup;
import com.ryuqq.marketplace.domain.category.vo.CategoryStatus;
import com.ryuqq.marketplace.domain.category.vo.Department;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CategorySearchCriteria 단위 테스트")
class CategorySearchCriteriaTest {

    @Nested
    @DisplayName("defaultCriteria() - 기본 검색 조건 생성")
    class DefaultCriteriaTest {

        @Test
        @DisplayName("기본 검색 조건은 필터 없는 상태로 생성된다")
        void createDefaultCriteria() {
            CategorySearchCriteria criteria = CategorySearchCriteria.defaultCriteria();

            assertThat(criteria.parentId()).isNull();
            assertThat(criteria.depth()).isNull();
            assertThat(criteria.leaf()).isNull();
            assertThat(criteria.statuses()).isEmpty();
            assertThat(criteria.departments()).isEmpty();
            assertThat(criteria.categoryGroups()).isEmpty();
            assertThat(criteria.searchField()).isNull();
            assertThat(criteria.searchWord()).isNull();
        }

        @Test
        @DisplayName("기본 검색 조건은 QueryContext를 가진다")
        void defaultCriteriaHasQueryContext() {
            CategorySearchCriteria criteria = CategorySearchCriteria.defaultCriteria();

            assertThat(criteria.queryContext()).isNotNull();
        }
    }

    @Nested
    @DisplayName("activeOnly() - 활성 카테고리 조건 생성")
    class ActiveOnlyTest {

        @Test
        @DisplayName("activeOnly는 ACTIVE 상태 필터를 가진다")
        void activeOnlyCriteriaHasActiveStatus() {
            CategorySearchCriteria criteria = CategorySearchCriteria.activeOnly();

            assertThat(criteria.statuses()).containsExactly(CategoryStatus.ACTIVE);
            assertThat(criteria.hasStatusFilter()).isTrue();
        }
    }

    @Nested
    @DisplayName("byParent() - 부모 카테고리 조건 생성")
    class ByParentTest {

        @Test
        @DisplayName("byParent는 부모 ID 필터와 ACTIVE 상태를 가진다")
        void byParentCriteriaHasParentAndActiveStatus() {
            CategorySearchCriteria criteria = CategorySearchCriteria.byParent(1L);

            assertThat(criteria.parentId()).isEqualTo(1L);
            assertThat(criteria.statuses()).containsExactly(CategoryStatus.ACTIVE);
            assertThat(criteria.hasParentFilter()).isTrue();
        }
    }

    @Nested
    @DisplayName("byCategoryGroup() - 카테고리 그룹 조건 생성")
    class ByCategoryGroupTest {

        @Test
        @DisplayName("byCategoryGroup은 카테고리 그룹 필터와 ACTIVE 상태를 가진다")
        void byCategoryGroupCriteriaHasGroupAndActiveStatus() {
            CategorySearchCriteria criteria =
                    CategorySearchCriteria.byCategoryGroup(CategoryGroup.CLOTHING);

            assertThat(criteria.categoryGroups()).containsExactly(CategoryGroup.CLOTHING);
            assertThat(criteria.hasCategoryGroupFilter()).isTrue();
            assertThat(criteria.statuses()).containsExactly(CategoryStatus.ACTIVE);
        }
    }

    @Nested
    @DisplayName("of() - 직접 생성")
    class OfTest {

        @Test
        @DisplayName("모든 필터를 포함한 검색 조건을 생성한다")
        void createWithAllFilters() {
            List<CategoryStatus> statuses = List.of(CategoryStatus.ACTIVE);
            List<Department> departments = List.of(Department.FASHION);
            List<CategoryGroup> groups = List.of(CategoryGroup.CLOTHING);
            QueryContext<CategorySortKey> queryContext =
                    QueryContext.defaultOf(CategorySortKey.defaultKey());

            CategorySearchCriteria criteria =
                    CategorySearchCriteria.of(
                            1L,
                            1,
                            true,
                            statuses,
                            departments,
                            groups,
                            CategorySearchField.NAME_KO,
                            "패션",
                            queryContext);

            assertThat(criteria.parentId()).isEqualTo(1L);
            assertThat(criteria.depth()).isEqualTo(1);
            assertThat(criteria.leaf()).isTrue();
            assertThat(criteria.statuses()).containsExactly(CategoryStatus.ACTIVE);
            assertThat(criteria.departments()).containsExactly(Department.FASHION);
            assertThat(criteria.categoryGroups()).containsExactly(CategoryGroup.CLOTHING);
            assertThat(criteria.searchField()).isEqualTo(CategorySearchField.NAME_KO);
            assertThat(criteria.searchWord()).isEqualTo("패션");
        }

        @Test
        @DisplayName("null 리스트들은 빈 리스트로 처리된다")
        void nullListsBecomeEmpty() {
            CategorySearchCriteria criteria =
                    CategorySearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(CategorySortKey.defaultKey()));

            assertThat(criteria.statuses()).isEmpty();
            assertThat(criteria.departments()).isEmpty();
            assertThat(criteria.categoryGroups()).isEmpty();
        }
    }

    @Nested
    @DisplayName("필터 유무 확인 메서드")
    class FilterCheckTest {

        @Test
        @DisplayName("hasParentFilter: 부모 ID가 있으면 true다")
        void hasParentFilterReturnsTrueWhenParentIdExists() {
            CategorySearchCriteria criteria = CategorySearchCriteria.byParent(1L);

            assertThat(criteria.hasParentFilter()).isTrue();
        }

        @Test
        @DisplayName("hasDepthFilter: 깊이 필터가 있으면 true다")
        void hasDepthFilterReturnsTrueWhenDepthExists() {
            CategorySearchCriteria criteria =
                    CategorySearchCriteria.of(
                            null,
                            2,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(CategorySortKey.defaultKey()));

            assertThat(criteria.hasDepthFilter()).isTrue();
        }

        @Test
        @DisplayName("hasLeafFilter: 리프 필터가 있으면 true다")
        void hasLeafFilterReturnsTrueWhenLeafExists() {
            CategorySearchCriteria criteria =
                    CategorySearchCriteria.of(
                            null,
                            null,
                            true,
                            null,
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(CategorySortKey.defaultKey()));

            assertThat(criteria.hasLeafFilter()).isTrue();
        }

        @Test
        @DisplayName("hasDepartmentFilter: 부문 필터가 있으면 true다")
        void hasDepartmentFilterReturnsTrueWhenDepartmentsExist() {
            CategorySearchCriteria criteria =
                    CategorySearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            List.of(Department.FASHION),
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(CategorySortKey.defaultKey()));

            assertThat(criteria.hasDepartmentFilter()).isTrue();
        }

        @Test
        @DisplayName("hasSearchCondition: 검색어가 있으면 true다")
        void hasSearchConditionReturnsTrueWhenSearchWordExists() {
            CategorySearchCriteria criteria =
                    CategorySearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            CategorySearchField.NAME_KO,
                            "패션",
                            QueryContext.defaultOf(CategorySortKey.defaultKey()));

            assertThat(criteria.hasSearchCondition()).isTrue();
        }

        @Test
        @DisplayName("hasSearchCondition: 공백 검색어는 false다")
        void hasSearchConditionReturnsFalseWhenSearchWordIsBlank() {
            CategorySearchCriteria criteria =
                    CategorySearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            "   ",
                            QueryContext.defaultOf(CategorySortKey.defaultKey()));

            assertThat(criteria.hasSearchCondition()).isFalse();
        }

        @Test
        @DisplayName("hasSearchField: 검색 필드가 있으면 true다")
        void hasSearchFieldReturnsTrueWhenSearchFieldExists() {
            CategorySearchCriteria criteria =
                    CategorySearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            CategorySearchField.CODE,
                            null,
                            QueryContext.defaultOf(CategorySortKey.defaultKey()));

            assertThat(criteria.hasSearchField()).isTrue();
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("statuses 리스트는 외부 변경에 영향을 받지 않는다")
        void statusListIsImmutable() {
            List<CategoryStatus> mutableStatuses = new ArrayList<>(List.of(CategoryStatus.ACTIVE));

            CategorySearchCriteria criteria =
                    CategorySearchCriteria.of(
                            null,
                            null,
                            null,
                            mutableStatuses,
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(CategorySortKey.defaultKey()));

            mutableStatuses.add(CategoryStatus.INACTIVE);

            assertThat(criteria.statuses()).hasSize(1);
            assertThat(criteria.statuses()).containsOnly(CategoryStatus.ACTIVE);
        }

        @Test
        @DisplayName("categoryGroups 리스트는 외부 변경에 영향을 받지 않는다")
        void categoryGroupsListIsImmutable() {
            List<CategoryGroup> mutableGroups = new ArrayList<>(List.of(CategoryGroup.CLOTHING));

            CategorySearchCriteria criteria =
                    CategorySearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            null,
                            mutableGroups,
                            null,
                            null,
                            QueryContext.defaultOf(CategorySortKey.defaultKey()));

            mutableGroups.add(CategoryGroup.SHOES);

            assertThat(criteria.categoryGroups()).hasSize(1);
            assertThat(criteria.categoryGroups()).containsOnly(CategoryGroup.CLOTHING);
        }
    }

    @Nested
    @DisplayName("페이징 편의 메서드 테스트")
    class PagingTest {

        @Test
        @DisplayName("size, offset, page 편의 메서드가 올바른 값을 반환한다")
        void pagingMethodsReturnCorrectValues() {
            CategorySearchCriteria criteria = CategorySearchCriteria.defaultCriteria();

            assertThat(criteria.size()).isPositive();
            assertThat(criteria.offset()).isGreaterThanOrEqualTo(0L);
            assertThat(criteria.page()).isGreaterThanOrEqualTo(0);
        }
    }
}
