package com.ryuqq.marketplace.adapter.out.persistence.category.condition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.domain.category.query.CategorySearchCriteria;
import com.ryuqq.marketplace.domain.category.query.CategorySearchField;
import com.ryuqq.marketplace.domain.category.vo.CategoryStatus;
import com.ryuqq.marketplace.domain.category.vo.Department;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CategoryConditionBuilderTest - 카테고리 조건 빌더 단위 테스트.
 *
 * <p>PER-CND-002: 각 조건은 BooleanExpression 반환.
 *
 * <p>PER-CND-003: null 입력 시 null 반환 (동적 쿼리 지원).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryConditionBuilder 단위 테스트")
class CategoryConditionBuilderTest {

    private CategoryConditionBuilder conditionBuilder;

    @Mock private CategorySearchCriteria criteria;

    @BeforeEach
    void setUp() {
        conditionBuilder = new CategoryConditionBuilder();
    }

    // ========================================================================
    // 1. idEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("idEq 메서드 테스트")
    class IdEqTest {

        @Test
        @DisplayName("유효한 ID 입력 시 BooleanExpression을 반환합니다")
        void idEq_WithValidId_ReturnsBooleanExpression() {
            // given
            Long id = 1L;

            // when
            BooleanExpression result = conditionBuilder.idEq(id);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null ID 입력 시 null을 반환합니다")
        void idEq_WithNullId_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.idEq(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 2. parentIdEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("parentIdEq 메서드 테스트")
    class ParentIdEqTest {

        @Test
        @DisplayName("부모 ID 필터가 있으면 BooleanExpression을 반환합니다")
        void parentIdEq_WithParentFilter_ReturnsBooleanExpression() {
            // given
            given(criteria.hasParentFilter()).willReturn(true);
            given(criteria.parentId()).willReturn(1L);

            // when
            BooleanExpression result = conditionBuilder.parentIdEq(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("부모 ID 필터가 없으면 null을 반환합니다")
        void parentIdEq_WithoutParentFilter_ReturnsNull() {
            // given
            given(criteria.hasParentFilter()).willReturn(false);

            // when
            BooleanExpression result = conditionBuilder.parentIdEq(criteria);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 3. depthEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("depthEq 메서드 테스트")
    class DepthEqTest {

        @Test
        @DisplayName("깊이 필터가 있으면 BooleanExpression을 반환합니다")
        void depthEq_WithDepthFilter_ReturnsBooleanExpression() {
            // given
            given(criteria.hasDepthFilter()).willReturn(true);
            given(criteria.depth()).willReturn(1);

            // when
            BooleanExpression result = conditionBuilder.depthEq(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("깊이 필터가 없으면 null을 반환합니다")
        void depthEq_WithoutDepthFilter_ReturnsNull() {
            // given
            given(criteria.hasDepthFilter()).willReturn(false);

            // when
            BooleanExpression result = conditionBuilder.depthEq(criteria);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 4. leafEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("leafEq 메서드 테스트")
    class LeafEqTest {

        @Test
        @DisplayName("리프 필터가 있으면 BooleanExpression을 반환합니다")
        void leafEq_WithLeafFilter_ReturnsBooleanExpression() {
            // given
            given(criteria.hasLeafFilter()).willReturn(true);
            given(criteria.leaf()).willReturn(true);

            // when
            BooleanExpression result = conditionBuilder.leafEq(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("리프 필터가 없으면 null을 반환합니다")
        void leafEq_WithoutLeafFilter_ReturnsNull() {
            // given
            given(criteria.hasLeafFilter()).willReturn(false);

            // when
            BooleanExpression result = conditionBuilder.leafEq(criteria);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 5. statusIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("statusIn 메서드 테스트")
    class StatusInTest {

        @Test
        @DisplayName("상태 필터가 있으면 BooleanExpression을 반환합니다")
        void statusIn_WithStatusFilter_ReturnsBooleanExpression() {
            // given
            given(criteria.hasStatusFilter()).willReturn(true);
            given(criteria.statuses()).willReturn(List.of(CategoryStatus.ACTIVE));

            // when
            BooleanExpression result = conditionBuilder.statusIn(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("상태 필터가 없으면 null을 반환합니다")
        void statusIn_WithoutStatusFilter_ReturnsNull() {
            // given
            given(criteria.hasStatusFilter()).willReturn(false);

            // when
            BooleanExpression result = conditionBuilder.statusIn(criteria);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("여러 상태 필터가 있으면 BooleanExpression을 반환합니다")
        void statusIn_WithMultipleStatuses_ReturnsBooleanExpression() {
            // given
            given(criteria.hasStatusFilter()).willReturn(true);
            given(criteria.statuses())
                    .willReturn(List.of(CategoryStatus.ACTIVE, CategoryStatus.INACTIVE));

            // when
            BooleanExpression result = conditionBuilder.statusIn(criteria);

            // then
            assertThat(result).isNotNull();
        }
    }

    // ========================================================================
    // 6. departmentIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("departmentIn 메서드 테스트")
    class DepartmentInTest {

        @Test
        @DisplayName("부문 필터가 있으면 BooleanExpression을 반환합니다")
        void departmentIn_WithDepartmentFilter_ReturnsBooleanExpression() {
            // given
            given(criteria.hasDepartmentFilter()).willReturn(true);
            given(criteria.departments()).willReturn(List.of(Department.FASHION));

            // when
            BooleanExpression result = conditionBuilder.departmentIn(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("부문 필터가 없으면 null을 반환합니다")
        void departmentIn_WithoutDepartmentFilter_ReturnsNull() {
            // given
            given(criteria.hasDepartmentFilter()).willReturn(false);

            // when
            BooleanExpression result = conditionBuilder.departmentIn(criteria);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("여러 부문 필터가 있으면 BooleanExpression을 반환합니다")
        void departmentIn_WithMultipleDepartments_ReturnsBooleanExpression() {
            // given
            given(criteria.hasDepartmentFilter()).willReturn(true);
            given(criteria.departments())
                    .willReturn(List.of(Department.FASHION, Department.BEAUTY));

            // when
            BooleanExpression result = conditionBuilder.departmentIn(criteria);

            // then
            assertThat(result).isNotNull();
        }
    }

    // ========================================================================
    // 7. searchCondition 테스트
    // ========================================================================

    @Nested
    @DisplayName("searchCondition 메서드 테스트")
    class SearchConditionTest {

        @Test
        @DisplayName("검색 조건이 있으면 BooleanExpression을 반환합니다")
        void searchCondition_WithSearchCondition_ReturnsBooleanExpression() {
            // given
            given(criteria.hasSearchCondition()).willReturn(true);
            given(criteria.searchWord()).willReturn("테스트");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("검색 조건이 없으면 null을 반환합니다")
        void searchCondition_WithoutSearchCondition_ReturnsNull() {
            // given
            given(criteria.hasSearchCondition()).willReturn(false);

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("CODE 필드로 검색 시 BooleanExpression을 반환합니다")
        void searchCondition_WithCodeField_ReturnsBooleanExpression() {
            // given
            given(criteria.hasSearchCondition()).willReturn(true);
            given(criteria.hasSearchField()).willReturn(true);
            given(criteria.searchField()).willReturn(CategorySearchField.CODE);
            given(criteria.searchWord()).willReturn("CAT001");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("NAME_KO 필드로 검색 시 BooleanExpression을 반환합니다")
        void searchCondition_WithNameKoField_ReturnsBooleanExpression() {
            // given
            given(criteria.hasSearchCondition()).willReturn(true);
            given(criteria.hasSearchField()).willReturn(true);
            given(criteria.searchField()).willReturn(CategorySearchField.NAME_KO);
            given(criteria.searchWord()).willReturn("테스트");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("NAME_EN 필드로 검색 시 BooleanExpression을 반환합니다")
        void searchCondition_WithNameEnField_ReturnsBooleanExpression() {
            // given
            given(criteria.hasSearchCondition()).willReturn(true);
            given(criteria.hasSearchField()).willReturn(true);
            given(criteria.searchField()).willReturn(CategorySearchField.NAME_EN);
            given(criteria.searchWord()).willReturn("Test");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("검색 필드가 없으면 통합 검색 BooleanExpression을 반환합니다")
        void searchCondition_WithoutSearchField_ReturnsUnifiedSearchExpression() {
            // given
            given(criteria.hasSearchCondition()).willReturn(true);
            given(criteria.hasSearchField()).willReturn(false);
            given(criteria.searchWord()).willReturn("테스트");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }
    }

    // ========================================================================
    // 8. pathStartsWithAny 테스트
    // ========================================================================

    @Nested
    @DisplayName("pathStartsWithAny 메서드 테스트")
    class PathStartsWithAnyTest {

        @Test
        @DisplayName("path prefix 목록이 있으면 BooleanExpression을 반환합니다")
        void pathStartsWithAny_WithPrefixes_ReturnsBooleanExpression() {
            // given
            List<String> prefixes = List.of("1", "1/10");

            // when
            BooleanExpression result = conditionBuilder.pathStartsWithAny(prefixes);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("단일 prefix로 BooleanExpression을 반환합니다")
        void pathStartsWithAny_WithSinglePrefix_ReturnsBooleanExpression() {
            // given
            List<String> prefixes = List.of("1");

            // when
            BooleanExpression result = conditionBuilder.pathStartsWithAny(prefixes);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("빈 목록이면 null을 반환합니다")
        void pathStartsWithAny_WithEmptyList_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.pathStartsWithAny(List.of());

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("null이면 null을 반환합니다")
        void pathStartsWithAny_WithNull_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.pathStartsWithAny(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 9. notDeleted 테스트
    // ========================================================================

    @Nested
    @DisplayName("notDeleted 메서드 테스트")
    class NotDeletedTest {

        @Test
        @DisplayName("BooleanExpression을 반환합니다")
        void notDeleted_Always_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.notDeleted();

            // then
            assertThat(result).isNotNull();
        }
    }
}
