package com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.condition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.domain.canonicaloption.query.CanonicalOptionGroupSearchCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CanonicalOptionGroupConditionBuilderTest - 캐노니컬 옵션 그룹 조건 빌더 단위 테스트.
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
@DisplayName("CanonicalOptionGroupConditionBuilder 단위 테스트")
class CanonicalOptionGroupConditionBuilderTest {

    private CanonicalOptionGroupConditionBuilder conditionBuilder;

    @Mock private CanonicalOptionGroupSearchCriteria criteria;

    @BeforeEach
    void setUp() {
        conditionBuilder = new CanonicalOptionGroupConditionBuilder();
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
    // 2. activeEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("activeEq 메서드 테스트")
    class ActiveEqTest {

        @Test
        @DisplayName("활성 필터가 있으면 BooleanExpression을 반환합니다")
        void activeEq_WithActiveFilter_ReturnsBooleanExpression() {
            // given
            given(criteria.hasActiveFilter()).willReturn(true);
            given(criteria.active()).willReturn(true);

            // when
            BooleanExpression result = conditionBuilder.activeEq(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("활성 필터가 없으면 null을 반환합니다")
        void activeEq_WithoutActiveFilter_ReturnsNull() {
            // given
            given(criteria.hasActiveFilter()).willReturn(false);

            // when
            BooleanExpression result = conditionBuilder.activeEq(criteria);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("비활성 필터가 있으면 BooleanExpression을 반환합니다")
        void activeEq_WithInactiveFilter_ReturnsBooleanExpression() {
            // given
            given(criteria.hasActiveFilter()).willReturn(true);
            given(criteria.active()).willReturn(false);

            // when
            BooleanExpression result = conditionBuilder.activeEq(criteria);

            // then
            assertThat(result).isNotNull();
        }
    }

    // ========================================================================
    // 3. searchCondition 테스트
    // ========================================================================

    @Nested
    @DisplayName("searchCondition 메서드 테스트")
    class SearchConditionTest {

        @Test
        @DisplayName("검색 필터가 있으면 BooleanExpression을 반환합니다")
        void searchCondition_WithSearchFilter_ReturnsBooleanExpression() {
            // given
            given(criteria.hasSearchFilter()).willReturn(true);
            given(criteria.searchField()).willReturn("CODE");
            given(criteria.searchWord()).willReturn("COLOR");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("검색 필터가 없으면 null을 반환합니다")
        void searchCondition_WithoutSearchFilter_ReturnsNull() {
            // given
            given(criteria.hasSearchFilter()).willReturn(false);

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("CODE 필드로 검색 시 BooleanExpression을 반환합니다")
        void searchCondition_WithCodeField_ReturnsBooleanExpression() {
            // given
            given(criteria.hasSearchFilter()).willReturn(true);
            given(criteria.searchField()).willReturn("CODE");
            given(criteria.searchWord()).willReturn("SIZE");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("NAME_KO 필드로 검색 시 BooleanExpression을 반환합니다")
        void searchCondition_WithNameKoField_ReturnsBooleanExpression() {
            // given
            given(criteria.hasSearchFilter()).willReturn(true);
            given(criteria.searchField()).willReturn("NAME_KO");
            given(criteria.searchWord()).willReturn("색상");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("NAME_EN 필드로 검색 시 BooleanExpression을 반환합니다")
        void searchCondition_WithNameEnField_ReturnsBooleanExpression() {
            // given
            given(criteria.hasSearchFilter()).willReturn(true);
            given(criteria.searchField()).willReturn("NAME_EN");
            given(criteria.searchWord()).willReturn("Color");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("알 수 없는 필드로 검색 시 null을 반환합니다")
        void searchCondition_WithUnknownField_ReturnsNull() {
            // given
            given(criteria.hasSearchFilter()).willReturn(true);
            given(criteria.searchField()).willReturn("UNKNOWN");
            given(criteria.searchWord()).willReturn("test");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 4. 통합 조건 테스트
    // ========================================================================

    @Nested
    @DisplayName("통합 조건 테스트")
    class CombinedConditionsTest {

        @Test
        @DisplayName("활성 필터와 검색 조건을 동시에 적용할 수 있습니다")
        void combinedConditions_WithActiveAndSearch_ReturnsBothExpressions() {
            // given
            given(criteria.hasActiveFilter()).willReturn(true);
            given(criteria.active()).willReturn(true);
            given(criteria.hasSearchFilter()).willReturn(true);
            given(criteria.searchField()).willReturn("CODE");
            given(criteria.searchWord()).willReturn("COLOR");

            // when
            BooleanExpression activeResult = conditionBuilder.activeEq(criteria);
            BooleanExpression searchResult = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(activeResult).isNotNull();
            assertThat(searchResult).isNotNull();
        }
    }
}
