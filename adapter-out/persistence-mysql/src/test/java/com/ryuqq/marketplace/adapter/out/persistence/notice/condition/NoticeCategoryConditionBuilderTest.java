package com.ryuqq.marketplace.adapter.out.persistence.notice.condition;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.notice.query.NoticeCategorySearchCriteria;
import com.ryuqq.marketplace.domain.notice.query.NoticeCategorySortKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * NoticeCategoryConditionBuilderTest - 공지사항 카테고리 조건 빌더 단위 테스트.
 *
 * <p>PER-COND-001: QueryDSL BooleanExpression 생성.
 *
 * <p>PER-COND-002: null-safe 조건 생성.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@DisplayName("NoticeCategoryConditionBuilder 단위 테스트")
class NoticeCategoryConditionBuilderTest {

    private NoticeCategoryConditionBuilder conditionBuilder;

    @BeforeEach
    void setUp() {
        conditionBuilder = new NoticeCategoryConditionBuilder();
    }

    private NoticeCategorySearchCriteria createCriteria(Boolean active, String searchField, String searchWord) {
        QueryContext<NoticeCategorySortKey> queryContext =
                QueryContext.defaultOf(NoticeCategorySortKey.defaultKey());
        return new NoticeCategorySearchCriteria(active, searchField, searchWord, queryContext);
    }

    // ========================================================================
    // 1. idEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("idEq 메서드 테스트")
    class IdEqTest {

        @Test
        @DisplayName("ID가 주어지면 BooleanExpression을 반환합니다")
        void idEq_WithValidId_ReturnsBooleanExpression() {
            // given
            Long id = 1L;

            // when
            BooleanExpression result = conditionBuilder.idEq(id);

            // then
            assertThat(result).isNotNull();
            assertThat(result.toString()).contains("noticeCategoryJpaEntity.id = 1");
        }

        @Test
        @DisplayName("ID가 null이면 null을 반환합니다")
        void idEq_WithNullId_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.idEq(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("0 ID도 올바르게 처리합니다")
        void idEq_WithZeroId_ReturnsBooleanExpression() {
            // given
            Long id = 0L;

            // when
            BooleanExpression result = conditionBuilder.idEq(id);

            // then
            assertThat(result).isNotNull();
        }
    }

    // ========================================================================
    // 2. targetCategoryGroupEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("targetCategoryGroupEq 메서드 테스트")
    class TargetCategoryGroupEqTest {

        @Test
        @DisplayName("타겟 카테고리 그룹이 주어지면 BooleanExpression을 반환합니다")
        void targetCategoryGroupEq_WithValidGroup_ReturnsBooleanExpression() {
            // given
            String targetCategoryGroup = "CLOTHING";

            // when
            BooleanExpression result =
                    conditionBuilder.targetCategoryGroupEq(targetCategoryGroup);

            // then
            assertThat(result).isNotNull();
            assertThat(result.toString())
                    .contains("noticeCategoryJpaEntity.targetCategoryGroup = CLOTHING");
        }

        @Test
        @DisplayName("타겟 카테고리 그룹이 null이면 null을 반환합니다")
        void targetCategoryGroupEq_WithNull_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.targetCategoryGroupEq(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 문자열은 BooleanExpression을 반환합니다")
        void targetCategoryGroupEq_WithEmptyString_ReturnsBooleanExpression() {
            // given
            String emptyString = "";

            // when
            BooleanExpression result = conditionBuilder.targetCategoryGroupEq(emptyString);

            // then
            assertThat(result).isNotNull();
        }
    }

    // ========================================================================
    // 3. activeEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("activeEq 메서드 테스트")
    class ActiveEqTest {

        @Test
        @DisplayName("활성 필터가 true이면 BooleanExpression을 반환합니다")
        void activeEq_WithActiveTrue_ReturnsBooleanExpression() {
            // given
            NoticeCategorySearchCriteria criteria = createCriteria(true, null, null);

            // when
            BooleanExpression result = conditionBuilder.activeEq(criteria);

            // then
            assertThat(result).isNotNull();
            assertThat(result.toString()).contains("noticeCategoryJpaEntity.active = true");
        }

        @Test
        @DisplayName("활성 필터가 false이면 BooleanExpression을 반환합니다")
        void activeEq_WithActiveFalse_ReturnsBooleanExpression() {
            // given
            NoticeCategorySearchCriteria criteria =
                    createCriteria(false, null, null);

            // when
            BooleanExpression result = conditionBuilder.activeEq(criteria);

            // then
            assertThat(result).isNotNull();
            assertThat(result.toString()).contains("noticeCategoryJpaEntity.active = false");
        }

        @Test
        @DisplayName("활성 필터가 없으면 null을 반환합니다")
        void activeEq_WithoutActiveFilter_ReturnsNull() {
            // given
            NoticeCategorySearchCriteria criteria =
                    createCriteria(null, null, null);

            // when
            BooleanExpression result = conditionBuilder.activeEq(criteria);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 4. searchCondition 테스트
    // ========================================================================

    @Nested
    @DisplayName("searchCondition 메서드 테스트")
    class SearchConditionTest {

        @Test
        @DisplayName("CODE 필드 검색 시 BooleanExpression을 반환합니다")
        void searchCondition_WithCodeField_ReturnsBooleanExpression() {
            // given
            NoticeCategorySearchCriteria criteria =
                    createCriteria(null, "CODE", "CLOTHING");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNotNull();
            assertThat(result.toString()).contains("CLOTHING");
        }

        @Test
        @DisplayName("NAME_KO 필드 검색 시 BooleanExpression을 반환합니다")
        void searchCondition_WithNameKoField_ReturnsBooleanExpression() {
            // given
            NoticeCategorySearchCriteria criteria =
                    createCriteria(null, "NAME_KO", "의류");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNotNull();
            assertThat(result.toString()).contains("의류");
        }

        @Test
        @DisplayName("NAME_EN 필드 검색 시 BooleanExpression을 반환합니다")
        void searchCondition_WithNameEnField_ReturnsBooleanExpression() {
            // given
            NoticeCategorySearchCriteria criteria =
                    createCriteria(null, "NAME_EN", "Clothing");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNotNull();
            assertThat(result.toString()).contains("Clothing");
        }

        @Test
        @DisplayName("검색 필터가 없으면 null을 반환합니다")
        void searchCondition_WithoutSearchFilter_ReturnsNull() {
            // given
            NoticeCategorySearchCriteria criteria =
                    createCriteria(null, null, null);

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("알 수 없는 필드는 null을 반환합니다")
        void searchCondition_WithUnknownField_ReturnsNull() {
            // given
            NoticeCategorySearchCriteria criteria =
                    createCriteria(null, "UNKNOWN_FIELD", "test");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 검색어는 null을 반환합니다")
        void searchCondition_WithEmptySearchWord_ReturnsNull() {
            // given
            NoticeCategorySearchCriteria criteria =
                    createCriteria(null, "CODE", "");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 5. 복합 조건 테스트
    // ========================================================================

    @Nested
    @DisplayName("복합 조건 테스트")
    class CombinedConditionTest {

        @Test
        @DisplayName("여러 조건을 AND로 결합할 수 있습니다")
        void combinedConditions_WithMultipleConditions_CanBeCombined() {
            // given
            Long id = 1L;
            String targetGroup = "CLOTHING";
            NoticeCategorySearchCriteria criteria =
                    createCriteria(true, null, null);

            // when
            BooleanExpression idCondition = conditionBuilder.idEq(id);
            BooleanExpression groupCondition =
                    conditionBuilder.targetCategoryGroupEq(targetGroup);
            BooleanExpression activeCondition = conditionBuilder.activeEq(criteria);

            BooleanExpression combined =
                    idCondition.and(groupCondition).and(activeCondition);

            // then
            assertThat(combined).isNotNull();
        }

        @Test
        @DisplayName("null 조건은 결합 시 무시됩니다")
        void combinedConditions_WithNullConditions_IgnoresNull() {
            // given
            Long id = 1L;
            NoticeCategorySearchCriteria criteria =
                    createCriteria(null, null, null);

            // when
            BooleanExpression idCondition = conditionBuilder.idEq(id);
            BooleanExpression groupCondition = conditionBuilder.targetCategoryGroupEq(null);
            BooleanExpression activeCondition = conditionBuilder.activeEq(criteria);

            // then
            assertThat(idCondition).isNotNull();
            assertThat(groupCondition).isNull();
            assertThat(activeCondition).isNull();
        }
    }
}
